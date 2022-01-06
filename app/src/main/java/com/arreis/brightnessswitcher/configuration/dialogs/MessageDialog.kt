package com.arreis.brightnessswitcher.configuration.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.arreis.brightnessswitcher.R

class MessageDialog : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage(requireArguments().getString("message"))
                .setPositiveButton(R.string.ok, null)
            return builder.create()
        }

        companion object {
            fun newInstance(_message: String?): MessageDialog {
                val f = MessageDialog()
                val args = Bundle()
                args.putString("message", _message)
                f.arguments = args
                return f
            }
        }
    }
