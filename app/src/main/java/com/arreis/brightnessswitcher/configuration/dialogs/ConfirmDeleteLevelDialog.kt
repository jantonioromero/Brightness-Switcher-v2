package com.arreis.brightnessswitcher.configuration.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.arreis.brightnessswitcher.R
import com.arreis.brightnessswitcher.configuration.MainActivity

class ConfirmDeleteLevelDialog : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage(R.string.deleteLevelConfirmationMessage)
                .setNegativeButton(R.string.no, null).setPositiveButton(
                    R.string.yes
                ) { dialog, which -> (activity as MainActivity?)!!.doDeleteSelectedLevel() }
            return builder.create()
        }
    }