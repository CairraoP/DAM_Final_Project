import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pt.ipt.api.R
import pt.ipt.api.model.Album
import pt.ipt.api.model.Music

class MusicAdapter(
    private val album: Album,
    private val musics: List<Music>,
    private val onPlayClicked: (Music) -> Unit
) : RecyclerView.Adapter<MusicAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val music = musics[position]
        holder.bindView(music)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.music_item, parent, false)
        return ViewHolder(view, album, onPlayClicked)
    }
    override fun getItemCount() = musics.size

    class ViewHolder(
        itemView: View,
        private val albumObj: Album,
        private val onPlayClicked: (Music) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val titulo: TextView = itemView.findViewById(R.id.titulo)
        private val playButton: ImageButton = itemView.findViewById(R.id.play_button)

        fun bindView(music: Music) {
            titulo.text = music.nome
            playButton.setOnClickListener {
                onPlayClicked(music)
            }
        }
    }
}
