import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.muhasib.snooq.R
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class FirstViewFragment : Fragment() {
    private lateinit var next: Button
    private lateinit var viewpager: ViewPager2
    private lateinit var skipBtn:TextView
   // private lateinit var dotsIndicator: DotsIndicator


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_first_view, container, false)

        // Initialize views
        next = view.findViewById(R.id.Next1ViewPager)
        skipBtn=view.findViewById(R.id.skip1ViewPager)

        // Use 'activity?.findViewById' instead of 'requireActivity()' for safety
        viewpager = activity?.findViewById(R.id.viewPager) ?: return view

      /*  dotsIndicator = view.findViewById(R.id.dotsIndicator)

        // Attach the dots indicator to the view pager
        dotsIndicator.attachTo(viewpager)*/

        next.setOnClickListener {
            // Ensure viewpager is valid and then navigate
            if (isAdded) {
                // Navigate to the next page
                val currentItem = viewpager.currentItem
                viewpager.currentItem =1
            }
        }

        skipBtn.setOnClickListener {
            if (isAdded) {
                findNavController().navigate(R.id.action_viewpagerfragment_to_signInFragment)
            }
        }
        return view
    }
}

