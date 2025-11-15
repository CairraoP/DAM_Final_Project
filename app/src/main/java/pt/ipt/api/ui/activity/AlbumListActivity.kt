package pt.ipt.api.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import pt.ipt.api.R
import pt.ipt.api.model.Album
import pt.ipt.api.model.GlobalVariables
import pt.ipt.api.retrofit.RetrofitInitializer
import pt.ipt.api.ui.adapter.AlbumListAdapter
import androidx.fragment.app.Fragment
import pt.ipt.api.retrofit.service.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AlbumListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_list)

        // Check if user is logged in
        if (TokenManager.getToken().isNullOrEmpty()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

       /* val button: Button = findViewById(R.id.button)
        button.setOnClickListener {
            listAlbums()
        }*/
    }

    private fun configureList(Albums: List<Album>) {
        val recyclerView: RecyclerView = findViewById(R.id.album_list_recyclerview)
        recyclerView.adapter = AlbumListAdapter(Albums, this)
        val layoutManager = StaggeredGridLayoutManager( 2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager
    }

    private fun listAlbums() {
        val call = RetrofitInitializer().albumService().list()
        call.enqueue(object : Callback<List<Album>?> {
            override fun onResponse(call: Call<List<Album>?>?, response: Response<List<Album>?>?) {
                Log.d("API", "Response code: ${response?.code()}")
                Log.d("API", "Body: ${response?.body()}")
                if (response?.isSuccessful == true && !response.body().isNullOrEmpty()) {
                    val albums = response.body()!!
                    configureList(albums)
                } else {
                    Toast.makeText(this@AlbumListActivity, "No albums found", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<Album>?>?, t: Throwable?) {
                t?.message?.let { Log.e("onFailure error", it) }
            }
        })
    }
}
