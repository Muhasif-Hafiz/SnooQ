import android.annotation.SuppressLint
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

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_first_view, container, false)

        // Initialize views
        next = view.findViewById(R.id.Next1ViewPager)

        // Ensure fragment is attached before using requireActivity
        if (isAdded) {
            viewpager = requireActivity().findViewById(R.id.viewPager)
        } else {
            // Handle case where fragment is not yet attached
            return view
        }

        dotsIndicator = view.findViewById(R.id.dotsIndicator)

        dotsIndicator.attachTo(viewpager)

        next.setOnClickListener {
            // Check if viewpager is attached
            if (isAdded) {
                viewpager.currentItem = 1 // Navigate to the next page
            }
        }

        return view
    }
}
