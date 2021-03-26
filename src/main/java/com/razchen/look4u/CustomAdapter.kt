package com.razchen.look4u


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class CustomAdapter(val listItem: ArrayList<FriendlyMessage>, val fromUserId: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var VIEW_HOLDER_ME: Int = 0
    var VIEW_HOLDER_YOU: Int = 1

    override fun getItemCount(): Int {

        return if (null != listItem) listItem!!.size else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (listItem!!.get(position).fromUserId.equals(fromUserId)) {
            VIEW_HOLDER_ME
        } else {
            VIEW_HOLDER_YOU
        }
    }


    override fun onBindViewHolder(v: RecyclerView.ViewHolder, pos: Int) {

        //Credit http://tutorials.jenkov.com/java-date-time/parsing-formatting-dates.html
        val d = Date(listItem!!.get(pos).timeStamp.toString().toLong());
        val format = SimpleDateFormat("dd/MM/yy HH:mm ")
        val dateString: String = format.format(d)

        if (v is ViewHolderMe) { // Handle Image Layout
            val viewHolderImage = v as ViewHolderMe
            viewHolderImage.messageBody!!.setText(String.format("%s",listItem!!.get(pos).text))
            viewHolderImage.itemView.tag = viewHolderImage
            viewHolderImage.timeStamp!!.setText(dateString);

        } else if (v is ViewHolderYou) { // Handle Video Layout
            val viewHolderYou = v as ViewHolderYou
            viewHolderYou.name!!.setText(String.format("%s", listItem!!.get(pos).name))
            viewHolderYou.messageBody!!.setText(String.format("%s", listItem!!.get(pos).text))
//            val drawable = viewHolderYou.avatar!!.getBackground() as GradientDrawable
//            drawable.setColor(Color.GRAY)
          //  Picasso.with(this).load(listItem!!.get(pos).userImageUrl)).into(viewHolderYou.avatar)

           // viewHolderYou.timeStamp!!.setText(String.format("%s", listItem!!.get(pos).timeStamp))


          //  val date: Date = format.parse("2009-12-31")
            viewHolderYou.timeStamp!!.setText(dateString);

//            Picasso
//                    .with(viewHolderYou.name.context) // give it the context
//                    .load("https://i.imgur.com/H981AN7.jpg") // load the image
//                    .into(viewHolderYou.avatar)

            Picasso
                    .with(viewHolderYou.name.context) // give it the context
                    .load(listItem!!.get(pos).userImageUrl) // load the image
                    .into(viewHolderYou.avatar)


//            viewHolderYou.itemView.tag = viewHolderYou
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder = when (viewType) {

            VIEW_HOLDER_ME -> return ViewHolderMe(
                LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.my_message,
                    parent,
                    false
                )
            )

            else  -> return ViewHolderYou(
                LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.their_message,
                    parent,
                    false
                )
            )
        }
        return return viewHolder
    }

    inner class ViewHolderMe(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val messageBody = itemView?.findViewById<TextView>(R.id.message_body)
        val timeStamp = itemView?.findViewById<TextView>(R.id.timeStampTextView)

    }

    inner class ViewHolderYou(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

//        val avatar = itemView?.findViewById<View>(R.id.avatar)
//        val avatar = itemView?.findViewById<ImageView>(R.id.avatar)

        val avatar = itemView?.findViewById<ImageView>(R.id.avatar)
        val name = itemView?.findViewById<TextView>(R.id.name)
        val messageBody = itemView?.findViewById<TextView>(R.id.message_body)
        val timeStamp = itemView?.findViewById<TextView>(R.id.timeStampTextView)

    }


}