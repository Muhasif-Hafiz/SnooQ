import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.muhasib.snooq.R
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class FirstViewFragment : Fragment() {
    private lateinit var next: Button
    private lateinit var viewpager: ViewPager2
    private lateinit var dotsIndicator: DotsIndicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout
        val view = inflater.inflate(R.layout.fragment_first_view, container, false)

        // Initialize views
        next = view.findViewById(R.id.Next1ViewPager)
        viewpager = requireActivity().findViewById(R.id.viewPager)
        dotsIndicator = view.findViewById(R.id.dotsIndicator)

        // Attach DotsIndicator to ViewPager2
        dotsIndicator.attachTo(viewpager)

        // Button click listener
        next.setOnClickListener {
            viewpager.currentItem = 1 // Navigate to the next page
        }

        return view
    }
}
