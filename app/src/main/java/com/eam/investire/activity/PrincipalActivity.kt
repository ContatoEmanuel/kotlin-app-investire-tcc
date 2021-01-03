package com.eam.investire.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eam.investire.R
import com.eam.investire.adapter.AdapterMovimentacao
import com.eam.investire.config.ConfiguracaoFirebase
import com.eam.investire.helper.Base64Custom
import com.eam.investire.model.Movimentacao
import com.eam.investire.model.Usuario
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.atomic.AtomicReference

class PrincipalActivity : AppCompatActivity() {
    private var calendarView: MaterialCalendarView? = null
    private var textoSaudacao: TextView? = null
    private var textoSaldo: TextView? = null
    private var despesaTotal = 0.0
    private var receitaTotal = 0.0
    private var resumoUsuario = 0.0
    private val autenticacao = ConfiguracaoFirebase.firebaseAutenticacao
    private val firebaseRef = ConfiguracaoFirebase.firebaseDatabase
    private var usuarioRef: DatabaseReference? = null
    private var adapterMovimentacao: AdapterMovimentacao? = null
    private var recyclerView: RecyclerView? = null
    private var valueEventListenerUsuario: ValueEventListener? = null
    private var valueEventListenerMovimentacoes: ValueEventListener? = null
    private val movimentacoes: MutableList<Movimentacao?> = ArrayList()
    private var movimentacao: Movimentacao? = null
    private var movimentacaoRef: DatabaseReference? = null
    private var mesAnoSelecionado: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Investire"
        setSupportActionBar(toolbar)
        textoSaldo = findViewById(R.id.textSaldo)
        textoSaudacao = findViewById(R.id.textSaudacao)
        calendarView = findViewById(R.id.calendarView)
        recyclerView = findViewById(R.id.recyclerMovimentos)
        configuraCalendarView()
        swipe()
        adapterMovimentacao = AdapterMovimentacao(movimentacoes, this)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.adapter = adapterMovimentacao
    }

    override fun onStart() {
        super.onStart()
        recuperarResumo()
        recuperarMovimentacoes()
    }

    override fun onStop() {
        super.onStop()
        usuarioRef!!.removeEventListener(valueEventListenerUsuario!!)
        movimentacaoRef!!.removeEventListener(valueEventListenerMovimentacoes!!)
    }

    private fun swipe() {
        val itemTouch: ItemTouchHelper.Callback = object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val dragFlags = ItemTouchHelper.ACTION_STATE_IDLE
                val swipeFlags = ItemTouchHelper.START
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                excluirMovimentacao(viewHolder)
            }
        }
        ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView)
    }

    fun excluirMovimentacao(viewHolder: RecyclerView.ViewHolder) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Excluir Movimentação da Conta")
        alertDialog.setMessage("Você tem certeza que deseja realmente excluir essa movimentação da sua conta?")
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton("Confirmar") { _: DialogInterface?, _: Int ->
            val position = viewHolder.adapterPosition
            movimentacao = movimentacoes[position]
            val emailUsuario = Objects.requireNonNull(autenticacao!!.currentUser)?.email!!
            val idUsuario = Base64Custom.codificarBase64(emailUsuario)
            movimentacaoRef = firebaseRef!!.child("movimentacao")
                    .child(idUsuario)
                    .child(mesAnoSelecionado!!)
            movimentacao!!.key?.let { movimentacaoRef!!.child(it).removeValue() }
            adapterMovimentacao!!.notifyItemRemoved(position)
            atualizarSaldo()
        }
        alertDialog.setNegativeButton("Cancelar") { _: DialogInterface?, _: Int ->
            Toast.makeText(this@PrincipalActivity,
                    "Cancelado",
                    Toast.LENGTH_SHORT).show()
            adapterMovimentacao!!.notifyDataSetChanged()
        }
        val alert = alertDialog.create()
        alert.show()
    }

    private fun atualizarSaldo() {
        val emailUsuario = Objects.requireNonNull(autenticacao!!.currentUser)?.email!!
        val idUsuario = Base64Custom.codificarBase64(emailUsuario)
        usuarioRef = firebaseRef!!.child("usuarios").child(idUsuario)
        if (movimentacao!!.tipo == "r") {
            receitaTotal -= movimentacao!!.valor
            usuarioRef!!.child("receitaTotal").setValue(receitaTotal)
        }
        if (movimentacao!!.tipo == "d") {
            despesaTotal -= movimentacao!!.valor
            usuarioRef!!.child("despesaTotal").setValue(despesaTotal)
        }
    }

    private fun recuperarMovimentacoes() {
        val emailUsuario = Objects.requireNonNull(autenticacao!!.currentUser)?.email!!
        val idUsuario = Base64Custom.codificarBase64(emailUsuario)
        movimentacaoRef = firebaseRef!!.child("movimentacao")
                .child(idUsuario)
                .child(mesAnoSelecionado!!)
        valueEventListenerMovimentacoes = movimentacaoRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                movimentacoes.clear()
                for (dados in snapshot.children) {
                    val movimentacao = dados.getValue(Movimentacao::class.java)
                    movimentacao!!.key = dados.key
                    movimentacoes.add(movimentacao)
                }
                adapterMovimentacao!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun recuperarResumo() {
        val emailUsuario = Objects.requireNonNull(autenticacao!!.currentUser)?.email!!
        val idUsuario = Base64Custom.codificarBase64(emailUsuario)
        usuarioRef = firebaseRef!!.child("usuarios").child(idUsuario)
        valueEventListenerUsuario = usuarioRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usuario = snapshot.getValue(Usuario::class.java)
                despesaTotal = usuario!!.despesaTotal
                receitaTotal = usuario.receitaTotal
                resumoUsuario = receitaTotal - despesaTotal
                val decimalFormat = DecimalFormat("#,##0.00")
                val resutadoFormatado = decimalFormat.format(resumoUsuario)
                val saudacao = "Olá, " + usuario.nome
                val saldo = "R$ $resutadoFormatado"
                textoSaudacao!!.text = saudacao
                textoSaldo!!.text = saldo
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_principal, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuSair) {
            autenticacao!!.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun adicionarDespesa(view: View?) {
        startActivity(Intent(this, DespesasActivity::class.java))
    }

    fun adicionarReceita(view: View?) {
        startActivity(Intent(this, ReceitasActivity::class.java))
    }

    private fun configuraCalendarView() {
        val meses = arrayOf<CharSequence>(
                "Janeiro",
                "Fevereiro",
                "Março",
                "Abril",
                "Maio",
                "Junho",
                "Julho",
                "Agosto",
                "Setembro",
                "Outrubro",
                "Novembro",
                "Dezembro"
        )
        calendarView!!.setTitleMonths(meses)
        val dataAtual = calendarView!!.currentDate
        val mesSelecionado = AtomicReference(String.format(Locale.getDefault(), "%02d", dataAtual.month))
        mesAnoSelecionado = mesSelecionado.toString() + "" + dataAtual.year
        calendarView!!.setOnMonthChangedListener { _: MaterialCalendarView?, date: CalendarDay ->
            mesSelecionado.set(String.format(Locale.getDefault(), "%02d", date.month))
            mesAnoSelecionado = mesSelecionado.toString() + "" + date.year
            movimentacaoRef!!.removeEventListener(valueEventListenerMovimentacoes!!)
            recuperarMovimentacoes()
        }
    }
}