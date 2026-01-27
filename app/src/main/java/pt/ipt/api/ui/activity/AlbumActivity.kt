package pt.ipt.api.ui.activity

import android.os.Bundle
import pt.ipt.api.databinding.ActivityAlbumBinding

class AlbumActivity : BaseActivity() {

    private lateinit var binding: ActivityAlbumBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        binding = ActivityAlbumBinding.inflate(layoutInflater)

        setContentViewChild(binding.root)
    }
}