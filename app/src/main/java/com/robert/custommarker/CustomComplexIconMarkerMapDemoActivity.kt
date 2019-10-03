package com.robert.custommarker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.OnMapReadyCallback
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.BitmapDescriptorFactory
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.MarkerOptions
import com.google.android.libraries.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.libraries.maps.GoogleMap.OnInfoWindowCloseListener
import com.google.android.libraries.maps.GoogleMap.OnMarkerClickListener
import com.google.android.libraries.maps.model.Marker


/**
 * This shows how to create a simple activity with a map and a marker on the map.
 */
class CustomComplexIconMarkerMapDemoActivity: AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener, OnInfoWindowClickListener, OnInfoWindowCloseListener, OnMarkerClickListener {

    val HANOI = LatLng(21.0200943, 105.7911019)
    val ZOOM_LEVEL = 13.5f
    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic_map_demo)
        val mapFragment : SupportMapFragment? =
                supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just move the camera to HANOI and add a marker in HANOI.
     */
    override fun onMapReady(googleMap: GoogleMap?) {
        // return early if the map was not initialised properly
        map = googleMap ?: return
        with(map) {
            // Set listeners for marker events.  See the bottom of this class for their behavior.
            setOnMarkerClickListener(this@CustomComplexIconMarkerMapDemoActivity)
            setOnInfoWindowClickListener(this@CustomComplexIconMarkerMapDemoActivity)
            setOnInfoWindowCloseListener(this@CustomComplexIconMarkerMapDemoActivity)
            setOnMapClickListener { }
            // Hide the zoom controls as the button panel will cover it.
            uiSettings.setAllGesturesEnabled(true)
            uiSettings.isCompassEnabled = true
            uiSettings.isMapToolbarEnabled = true
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isMyLocationButtonEnabled = true
            uiSettings.isIndoorLevelPickerEnabled = true
            moveCamera(CameraUpdateFactory.newLatLngZoom(HANOI, ZOOM_LEVEL))


            //addMarker(MarkerOptions().position(HANOI))
            addMarkersToMap()
        }

    }

    override fun onMapClick(latLong: LatLng) {

    }

    override fun onInfoWindowClick(marker: Marker) {
        Toast.makeText(this, "Click Info Window", Toast.LENGTH_SHORT).show()
    }

    override fun onInfoWindowClose(marker: Marker) {
        Toast.makeText(this, "Close Info Window", Toast.LENGTH_SHORT).show()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        Log.e("CustomComplexIconMarkerMapDemoActivity", "latitude=${marker.position.latitude},longitude=${marker.position.longitude},tag=${marker.tag.toString()}")
        return false
    }

    /**
     * Show all the specified markers on the map
     */
    private fun addComplexMarker(shopName: String, reviewNumber: String, shopUrl1: String, shopUrl2: String, latLng: LatLng, snippet: String, tag: String) {
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val inflatedFrame = inflater.inflate(R.layout.view_custom_marker, null)

        val shopPhoto1: RoundedImageView = inflatedFrame.findViewById(R.id.shop_photo)
        val shopPhoto2: RoundedImageView = inflatedFrame.findViewById(R.id.shop_photo_2)
        val badge: TextView = inflatedFrame.findViewById(R.id.badge)

        badge.text = reviewNumber

        Glide.with(this)
                .asBitmap()
                .load(shopUrl1)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(object : CustomTarget<Bitmap>(){
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        shopPhoto1.setImageBitmap(resource)
                    }
                    override fun onLoadCleared(placeholder: Drawable?) {
                        // this is called when imageView is cleared on lifecycle call or for
                        // some other reason.
                        // if you are referencing the bitmap somewhere else too other than this imageView
                        // clear it here as you can no longer have the bitmap
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        Log.e("CustomComplexIconMarkerMapDemoActivity", "Cannot load:$shopUrl1")
                    }
                })

        Glide.with(this)
                .asBitmap()
                .load(shopUrl2)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(object : CustomTarget<Bitmap>(){
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        shopPhoto2.setImageBitmap(resource)

                        val view: ConstraintLayout = inflatedFrame.findViewById(R.id.custom_marker_view)
                        val bitmapIcon = getBitmapFromView(view)

                        map.addMarker(MarkerOptions()
                                .position(latLng)
                                .title(shopName)
                                .snippet(snippet)
                                .icon(BitmapDescriptorFactory.fromBitmap(bitmapIcon))
                                .infoWindowAnchor(0.5f, 0.5f)
                                .draggable(true)
                        ).tag = tag
                    }
                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        Log.e("CustomComplexIconMarkerMapDemoActivity", "Cannot load:$shopUrl2")
                    }
                })


    }

    private fun addMarkersToMap() {
        addComplexMarker("219 Trung Kinh", "3",
                "https://scontent.fhan5-1.fna.fbcdn.net/v/t34.0-12/10585575_687148434697805_221520669_n.jpg?_nc_cat=109&_nc_oc=AQmQ-SmzfiArKUtPH0ma83PWG8DYK-FvnZkYVunKIfc_pr-0oea4LAxPVaAipGvpEgJsJEm5BJeuSCCtJBocEQ2i&_nc_ht=scontent.fhan5-1.fna&oh=81e42e72fc5b79fcfcff3b5f06ffa5ec&oe=5D8D569F",
                "https://we25.vn/media/images/o-anhvong3%20(4).jpg",
                HANOI,
                "Customize marker",
                "review_id_1"
        )

        addComplexMarker("Kange Name","5",
                "https://66.media.tumblr.com/a8b40c82ea3c700542b547c604fb98db/tumblr_pt3l5roG0v1yq3z6so1_500.jpg",
                "https://scontent-sin6-2.xx.fbcdn.net/v/t1.0-9/51137470_970240259835633_4276944475059650560_n.jpg?_nc_cat=111&_nc_oc=AQndTeOIHNMf4OsJcAedc1IT5TYwbO0XRaJDwl5R3NUfkhhCek4b0-D7W8Cs5YIdWNtFknIIzAbPN5_2odN-P3xh&_nc_ht=scontent-sin6-2.xx&oh=1b989325754c30b2555b0af551ee2a55&oe=5E01EC22",
                LatLng(21.017272,105.780847),
                "Customize marker",
                "review_id_2"
        )

        addComplexMarker("DH Ngoai Thuong HN", "7",
                "https://cdn.24h.com.vn/upload/4-2018/images/2018-11-11/1541939252-661-raver-1541815005-width714height1029.jpg",
                "https://scontent-sin6-2.xx.fbcdn.net/v/t1.15752-9/71241130_364170447824161_6466154696707932160_n.jpg?_nc_cat=109&_nc_oc=AQnPuAYt0J2vFn-GTD1PaUmbGIRGTmzqDsMyKV3WX1WMOreWYuBaqhSsGvHDREQJyYCVldhWgjJ6EzzXKgOFA30t&_nc_ht=scontent-sin6-2.xx&oh=707042f0e7643198bc7431ac66713462&oe=5E014050",
                LatLng(21.0248701,105.8019673),
                "Customize marker",
                "review_id_3"
        )

        addComplexMarker("DH Thuong Mai HN", "9",
                "https://i.imgur.com/DbTR5pI.jpg",
                "http://a9.vietbao.vn/images/vn899/150/2019/05/20190523-nhung-hotgirl-phong-gym-viet-voi-vong-3-nong-bong-khong-thua-gi-co-kim-4.jpg",
                LatLng(21.0304409,105.7849514),
                "Customize marker",
                "review_id_4"
        )

        addComplexMarker("Toa nha Trung Yen", "15",
                "https://i.pinimg.com/474x/dc/4f/79/dc4f79750b68e0563b68ba546f745acb.jpg",
                "https://scontent-sin6-2.xx.fbcdn.net/v/t1.0-9/22046982_1712727978806507_4686782754864974483_n.jpg?_nc_cat=109&_nc_oc=AQlIRvkI-LHuuAv9KBERLazokFXvPsFg7oZAEpT-Xdop1EMhuIO_q8qR25K34jgTQ3nAtaW-QJZYsaor42kv9FgF&_nc_ht=scontent-sin6-2.xx&oh=afcd99ad9de2a23ddf7d120f7c325281&oe=5DF2A917",
                LatLng(21.0165532,105.7977057),
                "Customize marker",
                "review_id_5"
        )
    }

    fun getBitmapFromView(v: View): Bitmap {
        v.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        v.layout(0, 0, v.measuredWidth, v.measuredHeight)
        val bitmap = Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)

        val c = Canvas(bitmap)
        v.layout(v.left, v.top, v.right, v.bottom)
        v.draw(c)
        return bitmap
    }

}
