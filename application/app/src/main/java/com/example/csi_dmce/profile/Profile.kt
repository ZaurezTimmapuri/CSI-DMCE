package com.example.csi_dmce.profile

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.csi_dmce.R
import com.example.csi_dmce.auth.CsiAuthWrapper
import com.example.csi_dmce.database.StudentWrapper
import kotlinx.coroutines.runBlocking

class Profile: AppCompatActivity() {
    private val REQUEST_CODE_IMAGE_UPSERT = 103
    private lateinit var imageUri: Uri?


    private lateinit var tvProfileName: TextView
    private lateinit var tvProfileStudentId: TextView
    private lateinit var etProfileName: EditText
    private lateinit var etStudentId: EditText
    private lateinit var etStudentClass: EditText
    private lateinit var etStudentContact: EditText
    private lateinit var ivStudentAvatar: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_profile)

        val studentObject = runBlocking { StudentWrapper.getStudent(CsiAuthWrapper.getStudentId(applicationContext))}

        tvProfileName = findViewById(R.id.text_view_profile_name)
        tvProfileName.setText(studentObject?.name)

        tvProfileStudentId = findViewById(R.id.text_view_profile_student_id)
        tvProfileStudentId.setText(studentObject?.student_id)

        etProfileName = findViewById(R.id.edit_text_profile_name)
        etProfileName.setText(studentObject?.name)

        etStudentId = findViewById(R.id.edit_text_profile_studentid)
        etStudentId.setText(studentObject?.student_id)

        etStudentClass = findViewById(R.id.edit_text_student_class)
        etStudentClass.setText(studentObject?.department + "-" + studentObject?.academic_year + "-" + studentObject?.division)

        etStudentContact = findViewById(R.id.edit_text_profile_mobile)
        etStudentContact.setText(studentObject?.phone_number.toString())

        ivStudentAvatar = findViewById(R.id.image_view_user_avatar)
        var avatarExists: Boolean = false

        runBlocking {
            StudentWrapper.getStudentAvatarUrl(studentObject?.student_id!!, studentObject.avatar_extension!!) {
                Glide.with(applicationContext)
                    .setDefaultRequestOptions(RequestOptions())
                    .load(it?: R.drawable.ic_baseline_person_24)
                    .into(ivStudentAvatar)

                avatarExists = true
            }
        }

        // When clicked on the avatar, it should show a upscaled version of it, if it exists.
        // Otherwise, open a prompt to pick a new image.
        ivStudentAvatar.setOnClickListener{
            if (!avatarExists) {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(intent, REQUEST_CODE_IMAGE_UPSERT)
            } else {
                val dialog = Dialog(this)
                dialog.setContentView(R.layout.component_image_scale_popup)
                val ivFullScale = dialog.findViewById<ImageView>(R.id.image_view_fullscale)
                ivFullScale.setImageDrawable(ivStudentAvatar.drawable)

                dialog.show()
                dialog.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE_UPSERT && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            ivStudentAvatar.setImageURI(imageUri)
        }
    }
}