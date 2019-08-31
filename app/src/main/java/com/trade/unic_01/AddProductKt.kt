package com.trade.unic_01

import com.trade.unic_01.dataclasses.ProductDetailskt
import kotlinx.android.synthetic.main.activity_add_shop.*



import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent

import android.net.Uri
import android.os.Build
import android.os.Bundle

import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

import com.facebook.spectrum.*
import com.facebook.spectrum.image.EncodedImageFormat
import com.facebook.spectrum.logging.SpectrumLogcatLogger
import com.facebook.spectrum.options.TranscodeOptions
import com.facebook.spectrum.requirements.EncodeRequirement
import com.facebook.spectrum.requirements.ResizeRequirement
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import com.trade.unic_01.dataclasses.ShopDetails
import kotlinx.android.synthetic.main.activity_add_product.*
import java.io.ByteArrayOutputStream
import kotlin.math.max


/**
 * This class doesn't follow the MVP structure
 * This is because by the time this class was written, all of us were so tired and frustrated that we thought to just write
 * a simple class, and make the code work as soon as possible
 */


class AddProductKt : AppCompatActivity() {

    var number : String? = null
    val REQUEST_IMAGE_GET = 1   //Request code for selecting an image from the gallery
    var ref : StorageReference? = null
    private var mSpectrum:Spectrum?= null
    private var prodName: EditText? = null
    private var prodPrice:EditText? = null
    private var prodCategory:EditText? = null
    private var qtyInStore:EditText? = null
    private var etdescription:EditText?=null
    private var etuserItemId:EditText?=null
    private var addImage: ImageButton?=null
    private var addProd: Button? = null
    private var imageSelectSucessfull=false
    private var photouri:Uri?=null
    private var mAuth: FirebaseAuth? = null
    private var shopid=""

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)
        mAuth=FirebaseAuth.getInstance()
        addImage = findViewById(R.id.prodImage)
        addProd = findViewById<Button>(R.id.addProd)
        prodCategory = findViewById<EditText>(R.id.prodCategory)
        prodName = findViewById<EditText>(R.id.prodName)
        prodPrice = findViewById<EditText>(R.id.prodPrice)
        qtyInStore = findViewById<EditText>(R.id.qtyInStore)
        etdescription=findViewById(R.id.description)
        etuserItemId=findViewById(R.id.userItemId)
        //hardcoded shopmid
        shopid="0oVaQgwGo2iHoTb8h6TT"//intent.getStringExtra("shopid")

        //Load default photo if profile image of user does not exist
        /* Glide.with(applicationContext).load(UserProfileData.UserProfileImage).apply(RequestOptions().placeholder(applicationContext.getDrawable(R.drawable.profile)))
                 .into(ProfileImageView)
 */
        //OnClick listener for the gallery button to open the gallery
        addImage!!.setOnClickListener {
            Log.d("Debug111" , "GalleryOpened")
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            startActivityForResult(intent, REQUEST_IMAGE_GET)
        }
        addProd!!.setOnClickListener(View.OnClickListener {
            val prodname = prodName!!.text.toString()
            val prodprice = prodPrice!!.text.toString()
            val quantity = qtyInStore!!.text.toString()
            val prodcategory = prodCategory!!.text.toString()
            val description= etdescription!!.text.toString()
            val useritemid=etuserItemId!!.text.toString()
            if (useritemid.isEmpty()||prodname.isEmpty()||prodprice.isEmpty()||quantity.isEmpty()||prodcategory.isEmpty()||description.isEmpty())
            {
                Toast.makeText(this,"Enter valid info",Toast.LENGTH_LONG).show()

            }
            else
            {
                if (imageSelectSucessfull)
                {
                    showLoadingStateactivity()
                    updateProdInfo(prodname,prodprice,quantity,prodcategory,description,useritemid,photouri!!)
                }
                else
                {
                    Toast.makeText(this,"Select Valid Image",Toast.LENGTH_LONG).show()
                }
            }


        })
    }

    /**
     * This function is called when the user clicks on the button to finish editing his about information
     * The function first makes the window untouchable and displays the progress bar
     * It then updates the data on the firestore account, and once the account is sucessfully updated, the local static variables
     * are also updated, so that the changes are reflected on the current app as well
     * TODO : When the user clicks on the edit text to edit the information, all the text gets wraped into a single line, making it difficult for the user to update.
     */

    private fun updateProdInfo(prodname: String,prodprice:String,quantity:String,prodcategory:String,description: String,useritemid:String,photouri:Uri?) {


        /*  val docData = mutableMapOf<String,String>()
          docData["ownerid"] = mAuth!!.uid!!*/
        var productDetails=ProductDetailskt(prodname,prodprice,quantity,prodcategory,"",description, useritemid,"")
        var uniqueItemId=""
        //showloadingstate
        Log.d("update","ayuth id ${mAuth!!.uid!!}")
        FirebaseFirestore.getInstance().collection("shops").document(shopid).collection("products").document().set(productDetails).addOnSuccessListener {

            FirebaseFirestore.getInstance().collection("shops").document(shopid).collection("products")
                    .whereEqualTo("useritemid",useritemid)
                    .whereEqualTo("prodname",prodname).get().addOnSuccessListener {docs->

                        for (doc in docs)
                        {
                            uniqueItemId=doc.id
                        }

                        // var shopDetails=ShopDetails(shopid,prodname,prodPrice,quantity,prodCategory,"")
                        updateImageOnFirebaseStorage(photouri,uniqueItemId,productDetails)
                    }.addOnFailureListener{
                        Toast.makeText(this,"Error geeeting uniqueitemid$it",Toast.LENGTH_LONG).show()
                        removeLoadingStateActivity()

                        Log.d("AddShop1","shopp doc crea failed$it")
                    }


            Log.d("Upadte","document with ownerid craeted")

        }.addOnFailureListener{

            Toast.makeText(this,"Error saving  prod details$it",Toast.LENGTH_LONG).show()
            removeLoadingStateActivity()

        }
    }

    /**
     * This function is called when the user selects an image from the gallery
     * The function first retrieves the phone number stored in the shared preferences because the static variables are destroyed
     * when the gallery is opened.
     * THen if there is no problem with the selected image, the image is first uploaded on the firebase storage, and then the function
     * [updateProfileImageOnDatabse] is called
     */

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("Addshop.kt" , "onActivity result")
        if (data!=null)
        {
            val fullPhotoUri: Uri? = data.data

            Log.d("ImageSearch" , fullPhotoUri.toString()+"resolv type ${data.resolveType(contentResolver)}")
            if (requestCode == 1 && resultCode == Activity.RESULT_OK && data.resolveType(contentResolver).toString().contains("image")) {
                //val thumbnail: Bitmap = data!!.getParcelableExtra("data")
                //showLoadingStateActivity()

                addImage!!.setImageURI(fullPhotoUri)
                photouri=fullPhotoUri
                imageSelectSucessfull=true

            }
            else
            {
                Toast.makeText(applicationContext , "Please select a valid image" , Toast.LENGTH_LONG).show()
            }
        } else
        {
            Toast.makeText(applicationContext , "No Image Selected" , Toast.LENGTH_LONG).show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    /**
     * This function is called after the user has sucessfully selected a profile image
     * The function retrives the URL of the image from firebase storage, and once that is sucessfull, it updates the url of the new image on the firebsase database,
     * as well as in the local variable [UserProfileData.UserProfileImage] which updates the image on the screen as well
     * The function then makes the screen touchable again and hides the progress bar
     */

    private fun updateImageOnFirebaseStorage(fullPhotoUri:Uri?,uniqueItemId:String,productDetailskt: ProductDetailskt){
        ref = FirebaseStorage.getInstance().reference

        val baos = ByteArrayOutputStream()

        val inputStream = contentResolver.openInputStream(fullPhotoUri!!)
        SpectrumSoLoader.init(this)
        val mSpectrum = Spectrum.make(
                SpectrumLogcatLogger(Log.INFO),
                DefaultPlugins.get()
        )
        mSpectrum!!.transcode(
                EncodedImageSource.from(inputStream), EncodedImageSink.from(baos),
                TranscodeOptions.Builder(
                        EncodeRequirement
                        (EncodedImageFormat.JPEG, EncodeRequirement.Mode.LOSSY)
                ).resize(ResizeRequirement.Mode.EXACT_OR_SMALLER, 720)
                        .build(), applicationContext
        )

        val data1 = baos.toByteArray()


/*
        FirebaseStorage.getInstance().getReference().child(mAuth!!.uid!!).child(shopid).child("shopimage").delete().addOnSuccessListener {
*/
        Log.d("Firebase Storage" , "Image  muth${mAuth!!.uid!!} shopid${shopid} uniqueitemid${uniqueItemId}")
        var uploadTask =  ref!!.child(mAuth!!.uid!!).child(shopid).child(uniqueItemId).child("productimage").putBytes(data1)
        uploadTask.addOnFailureListener {
            Log.d("Firebase Storage" , "Image upload error $it")
            Toast.makeText(this,"Error Uploading prod image$it",Toast.LENGTH_LONG).show()
            removeLoadingStateActivity()

            // Handle unsuccessful uploads
        }.addOnSuccessListener {
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...

            FirebaseStorage.getInstance().getReference().child(mAuth!!.uid!!).child(shopid).child(uniqueItemId).child("productimage")
                    .downloadUrl.addOnSuccessListener {

                Log.d("Firebase Storage" , "Image uploaded sucessfully")
                imageSelectSucessfull=false
               /* prod.imagelink=it.toString()*/
                updateShopDataOnFirestore(shopid,it.toString(),uniqueItemId,productDetailskt)

            }.addOnFailureListener{

                Toast.makeText(this,"Error downloading prod URL$it",Toast.LENGTH_LONG).show()
                removeLoadingStateActivity()

            }
        }


        //}
    }
    fun updateShopDataOnFirestore(shopid:String,imagelink:String,uniqueItemId: String,productDetailskt: ProductDetailskt)
    {
        val docData = mutableMapOf<String,String>()

        productDetailskt.imagelink=imagelink
        productDetailskt.uniqueitemid=uniqueItemId

        FirebaseFirestore.getInstance().collection("shops").document(shopid).collection("products")
                .document(uniqueItemId).set(productDetailskt).addOnCompleteListener {

                    FirebaseFirestore.getInstance().collection("shops").document(shopid).get().addOnSuccessListener{documentsnap->
                        var old= (documentsnap["noofproducts"] as String).toLong()

                        old+=1

                        FirebaseFirestore.getInstance().collection("shops").document(shopid).update("noofproducts",old.toString()).addOnSuccessListener {
                            removeLoadingStateActivity()
                            Toast.makeText(this,"products details uploaded",Toast.LENGTH_LONG).show()
                        }.addOnFailureListener{
                            Toast.makeText(this,"Error updating no of prod $it",Toast.LENGTH_LONG).show()
                            removeLoadingStateActivity()
                        }
                    }.addOnFailureListener{

                        Toast.makeText(this,"Error getting no of prod$it",Toast.LENGTH_LONG).show()
                        removeLoadingStateActivity()
                    }

        }.addOnFailureListener{
                    Toast.makeText(this,"Error setting prod details$it",Toast.LENGTH_LONG).show()
                    removeLoadingStateActivity()


                }



    }

    private fun showLoadingStateactivity(){
        prog_bar_add_prod.isIndeterminate
        prog_bar_add_prod.visibility = View.VISIBLE
        window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
    private fun removeLoadingStateActivity(){
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        prog_bar_add_prod.visibility = View.VISIBLE
    }
}
