package com.trade.unic_01


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
import kotlinx.android.synthetic.main.activity_add_shop.*
import java.io.ByteArrayOutputStream



/**
 * This class doesn't follow the MVP structure
 * This is because by the time this class was written, all of us were so tired and frustrated that we thought to just write
 * a simple class, and make the code work as soon as possible
 */


class AddShopKt : AppCompatActivity() {

    var number : String? = null
    val REQUEST_IMAGE_GET = 1   //Request code for selecting an image from the gallery
    var ref : StorageReference? = null
    private var mSpectrum:Spectrum?= null
    private var addImage: ImageButton? = null
    private var addShop: Button? = null
    private var etShopName: EditText? = null
    private var etLocality:EditText? = null
    private var etAddress:EditText? = null
    private var etCategory:EditText? = null
    private var imageSelectSucessfull=false
    private var photouri:Uri?=null
    private var mAuth: FirebaseAuth? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_shop)
        mAuth=FirebaseAuth.getInstance()
        etAddress = findViewById(R.id.shopAddress);
        etCategory = findViewById(R.id.shopCategory)
        etLocality = findViewById(R.id.shopLocality)
        etShopName = findViewById(R.id.shopName)
        addImage = findViewById(R.id.addImage)
        addShop = findViewById(R.id.addShop)
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
        addShop!!.setOnClickListener(View.OnClickListener {
            val shopname = etShopName!!.text.toString()
            val shopaddress = etAddress!!.text.toString()
            val shoplocality = etLocality!!.text.toString()
            val shopcategory = etCategory!!.text.toString()

            if (shopname.isEmpty()||shopaddress.isEmpty()||shoplocality.isEmpty()||shopcategory.isEmpty())
            {
                Toast.makeText(this,"Enter valid info",Toast.LENGTH_LONG).show()

            }
            else
            {
                if (imageSelectSucessfull)
                {
                    showLoadingStateactivity()
                    updateShopInfo(shopname,shopaddress,shoplocality,shopcategory,photouri!!)
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

    private fun updateShopInfo(shopname: String,shopaddress:String,shoplocality:String,shopcategory:String,photouri:Uri?) {


      /*  val docData = mutableMapOf<String,String>()
        docData["ownerid"] = mAuth!!.uid!!*/
        var shopDetails=ShopDetails(mAuth!!.uid!!,shopname,shopaddress,shoplocality,shopcategory,"","0","0","")

        var shopId=""
        //showloadingstate
Log.d("update","ayuth id ${mAuth!!.uid!!}")
       FirebaseFirestore.getInstance().collection("shops").document().set(shopDetails).addOnSuccessListener {

           FirebaseFirestore.getInstance().collection("shops").whereEqualTo("ownerid",mAuth!!.uid!!)
                   .whereEqualTo("shopname",shopname).get().addOnSuccessListener {docs->

                       for (doc in docs)
                       {
                            shopId=doc.id

                       }

                      // var shopDetails=ShopDetails(shopid,shopname,shopaddress,shoplocality,shopcategory,"")
                       updateImageOnFirebaseStorage(photouri,shopId,shopDetails)
                      /* FirebaseFirestore.getInstance().collection("users").document(mAuth!!.uid!!)
                               .collection("shopscreated").document(shopid).set().addOnSuccessListener {

                                   window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                   ProgressBarProfile.visibility=View.INVISIBLE
                                   Toast.makeText(applicationContext, "Updated your information successfully",Toast.LENGTH_SHORT).show()
                               }*/
                   }.addOnFailureListener{
                       Toast.makeText(this,"Error retriving ShopId$it",Toast.LENGTH_LONG).show()
                       removeLoadingStateActivity()
                           Log.d("AddShop1","shopp doc crea failed$it")
                        }


           Log.d("Upadte","document with ownerid craeted")

       }.addOnFailureListener{
           Toast.makeText(this,"Error Creating Shop$it",Toast.LENGTH_LONG).show()
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

    private fun updateImageOnFirebaseStorage(fullPhotoUri:Uri?,shopid:String,shopDetails:ShopDetails){
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

            var uploadTask =  ref!!.child(mAuth!!.uid!!).child(shopid).child("shopimage").putBytes(data1)
            uploadTask.addOnFailureListener {
                Toast.makeText(this,"Error uploading Image$it",Toast.LENGTH_LONG).show()
                removeLoadingStateActivity()
                // Handle unsuccessful uploads
            }.addOnSuccessListener {
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                // ...

                FirebaseStorage.getInstance().getReference().child(mAuth!!.uid!!).child(shopid).child("shopimage").downloadUrl.addOnSuccessListener {

                    Log.d("Firebase Storage" , "Image uploaded sucessfully")
                    imageSelectSucessfull=false
                    shopDetails.imagelink=it.toString()
                    updateShopDataOnFirestore(shopid,it.toString(),shopDetails)

                }.addOnFailureListener{

                    Toast.makeText(this,"Error downloading URL$it",Toast.LENGTH_LONG).show()
                    removeLoadingStateActivity()
                }
            }


        //}
    }
    fun updateShopDataOnFirestore(shopid: String,imagelink:String,shopDetails: ShopDetails)
    {
         val docData = mutableMapOf<String,String>()
        docData["imagelink"] = imagelink
        docData["shopname"]=shopDetails.shopname
        docData["shopid"]=shopid

        shopDetails.imagelink=imagelink
        shopDetails.shopid=shopid

        FirebaseFirestore.getInstance().collection("users").document(mAuth!!.uid!!).collection("shopscreated").
                document(shopid).set(docData).addOnCompleteListener {
            //window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
           Toast.makeText(this,"shop details iplaoded",Toast.LENGTH_LONG).show()
        }.addOnFailureListener{

            Toast.makeText(this,"Error Updating ShopsCreated$it",Toast.LENGTH_LONG).show()
            removeLoadingStateActivity()
        }

        FirebaseFirestore.getInstance().collection("shops").document(shopid).set(shopDetails).addOnSuccessListener{
            Toast.makeText(this,"shop details uploaded ",Toast.LENGTH_LONG).show()

            removeLoadingStateActivity()
        }.addOnFailureListener{

            Toast.makeText(this,"Error Updating Shop$it",Toast.LENGTH_LONG).show()
            removeLoadingStateActivity()
        }


    }

    private fun showLoadingStateactivity(){

        prog_bar_add_shop.isIndeterminate
        prog_bar_add_shop.visibility = View.VISIBLE
        window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

   private fun  removeLoadingStateActivity(){
        prog_bar_add_shop.visibility=View.INVISIBLE
       window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
   }
}

