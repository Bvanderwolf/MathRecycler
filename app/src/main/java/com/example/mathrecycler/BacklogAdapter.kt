package com.example.mathrecycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.playedgame_layout.view.*

class BacklogAdapter(private val backlog: List<PlayedGame>, private val onClick: (PlayedGame) -> Unit) :
    RecyclerView.Adapter<BacklogAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return backlog.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(backlog[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.playedgame_layout, parent, false)
        )
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        init {
            itemView.setOnClickListener{ onClick(backlog[adapterPosition]) }
        }

        fun bind(playedGame: PlayedGame){
            //binds playedGame values to itemView views
            var context = itemView.context
            itemView.tvDifficulty.text = context.getString(R.string.played_game_difficulty, playedGame.difficulty)
            itemView.tvOperator.text = context.getString(R.string.played_game_operator, playedGame.usedOperator)
            itemView.tvGameTime.text = context.getString(R.string.game_popup_gameTime, playedGame.gameTime)
            itemView.tvCorrectAnswers.text = context.getString(R.string.played_game_correct_answers, playedGame.correctAnswers.toString())
            itemView.tvWrongAnswers.text = context.getString(R.string.played_game_wrong_answers, playedGame.wrongAnswers.toString())
            itemView.tvPlayedGameDate.text = context.getString(R.string.played_game_date, playedGame.date)
        }
    }
}