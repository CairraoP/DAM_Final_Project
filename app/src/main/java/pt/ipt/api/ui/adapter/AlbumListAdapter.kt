package pt.ipt.api.ui.adapter
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.w3c.dom.Text
import pt.ipt.api.R
import pt.ipt.api.model.Album
import pt.ipt.api.model.GlobalVariables

class AlbumListAdapter(private val albums: List<Album>, private val context: Context) :
    RecyclerView.Adapter<AlbumListAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val album = albums[position]
        holder.bindView(album)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.album_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int{
        return albums.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val title: TextView = itemView.findViewById(R.id.album_item_titulo)
        val description: TextView = itemView.findViewById(R.id.album_item_description)
        val imgView: ImageView = itemView.findViewById(R.id.album_item_foto)

        fun bindView(album: Album) {

            title.text = album.titulo
            //temporario
            description.text = album.foto
            val imageUrl = GlobalVariables.CON_STRING+album.foto

            //Debug Line
            //Log.d("AlbumAdapter", "Image URL: $imageUrl")


            if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(itemView.context)
                        .load(imageUrl)
                        .into(imgView)
                } else {
                    imgView.setImageResource(R.drawable.ic_launcher_foreground) // optional placeholder
                }

        }

        }
    }

