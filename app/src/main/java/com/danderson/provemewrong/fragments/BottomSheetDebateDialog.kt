package com.danderson.provemewrong.fragments

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.danderson.provemewrong.R
import com.danderson.provemewrong.model.DebateLine
import com.danderson.provemewrong.model.User
import com.google.firebase.auth.FirebaseAuth

class BottomSheetDebateDialog: BottomSheetDialogFragment(){

    private var menuCallback: BottomSheetMenuCallbacks? = null

    interface BottomSheetMenuCallbacks{
        fun onLikeMessage(line: DebateLine)
        fun onEditMessage(line: DebateLine)
        fun onRemoveMessage(line: DebateLine)
        fun onViewProfile(line: DebateLine)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.dialog_bottom_sheet, container, false)
        val args = arguments!!
        val line = args.getSerializable(BottomSheetDebateDialog.ARG_LINE) as DebateLine

        val likeMessage: LinearLayout = v.findViewById(R.id.like_message)
        likeMessage.setOnClickListener {
            dismiss()
            menuCallback!!.onLikeMessage(line)
        }

        val editMessage: LinearLayout = v.findViewById(R.id.edit_message)
        editMessage.setOnClickListener {
            dismiss()
            menuCallback!!.onEditMessage(line)
        }

        val removeMessage: LinearLayout = v.findViewById(R.id.remove_message)
        removeMessage.setOnClickListener {
            dismiss()
            menuCallback!!.onRemoveMessage(line)
        }

        val viewProfile: LinearLayout = v.findViewById(R.id.view_profile)
        viewProfile.setOnClickListener {
            dismiss()
            menuCallback!!.onViewProfile(line)
        }

        // the user object is used to determine which menu options should be visible for the debate line
        if(line.user.id == FirebaseAuth.getInstance().currentUser!!.uid){
            likeMessage.visibility = View.GONE
            viewProfile.visibility = View.GONE
        }
        else{
            editMessage.visibility = View.GONE
            removeMessage.visibility = View.GONE
        }

        return v
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            menuCallback = context as BottomSheetMenuCallbacks
        } catch (e: ClassCastException){
            throw ClassCastException(context?.toString()
                    + "must implement BottomSheetMenuCallbacks interface")
        }
    }

    companion object {
        const val ARG_LINE = "line"

        @JvmStatic
        fun newInstance(line: DebateLine): BottomSheetDebateDialog {
            val args = Bundle()
            args.putSerializable(ARG_LINE, line)
            val fragment = BottomSheetDebateDialog()
            fragment.arguments = args
            return fragment
        }
    }
}