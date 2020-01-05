package com.example.mathrecycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.math_item_layout.view.*
import kotlin.random.Random

class MathItemAdapter(private val mathList: List<MathItem>, private val context: Context): RecyclerView.Adapter<MathItemAdapter.ViewHolder>(){

    private var animationPosition = -1

    private val shakeAnimList = arrayListOf<Animation>()
    var shakingList = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.math_item_layout, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return mathList.count()
    }

    //sets animation position to be last index of math list
    fun updateAnimationPosition(){
        animationPosition = mathList.count() - 1
    }

    /*resets animation to -1 to make sure no holders get animated
    and removes removes animation from shakeAnimList after canceling it*/
    fun removeAnimation(removedIndex: Int){
        animationPosition = -1
        shakeAnimList[removedIndex].cancel()
        shakeAnimList.removeAt(removedIndex)
    }

    //cancels all animations in shakeAnimList and sets shake List bool to false
    fun stopFadingList(){
        if(shakingList){
            shakingList = false
            for(anim in shakeAnimList)anim.cancel()
        }
    }
    /*when binding a view to the recycler we check whether or not it can be animated
    or not. Based on if it is added newly to the adapter or needs to shake, it will do
    animations*/
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mathList[position])

        //if its position is equal to the animationPosition, which would be the last position
        //in the mathItem list, it starts its appear animation.
        if(position == animationPosition){
            val appearAnimation = AnimationUtils.loadAnimation(context, R.anim.math_item_animation)
            appearAnimation.setAnimationListener(object: Animation.AnimationListener{
                override fun onAnimationRepeat(p0: Animation?) {}

                /*When the appearing math item finished appearing it
                needs to check whether it needs to shaking or not*/
                override fun onAnimationEnd(p0: Animation?) {
                    if(shakingList){
                        val animatePos = if(animationPosition == -1) mathList.count() - 1 else animationPosition
                        holder.itemView.startAnimation(shakeAnimList[animatePos])
                    }
                }
                override fun onAnimationStart(p0: Animation?) {}
            })
            holder.itemView.startAnimation(appearAnimation)

            //when this item has been newly added to the list it get's its own shake animation with a random startOffset
            val shakeAnimation = AnimationUtils.loadAnimation(context, R.anim.math_item_shake_animation)
            shakeAnimation.startOffset = Random.nextLong(1000)
            shakeAnimList.add(shakeAnimation)
        }
        else{
            /*if the item is not the newly added item we need to check whether it needs
            to start shaking or not*/
            if(shakingList){
                holder.itemView.startAnimation(shakeAnimList[position])
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(mathItem: MathItem){
            itemView.tvNumLeftHand.text = mathItem.leftHandArg.toString()
            itemView.tvNumRightHand.text = mathItem.rightHandArg.toString()
            itemView.tvBestOperator.text = mathItem.operator
            itemView.tvProposedAnswer.text = mathItem.proposedAnswer.toString().split(".")[0]
        }
    }
}