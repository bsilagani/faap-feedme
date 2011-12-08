package de.faap.feedme.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.actionbarcompat.ActionBarActivity;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitleProvider;

import de.faap.feedme.R;
import de.faap.feedme.provider.IRecipeProvider;
import de.faap.feedme.provider.ProxyRecipeProvider;
import de.faap.feedme.util.Recipe;

public class PreperationActivity extends ActionBarActivity {
    static final int NUM_ITEMS = 2;

    static Context mContext;
    static Recipe recipe;

    private IRecipeProvider db;
    private mFPAdapter mFPAdapter;
    private ViewPager mViewPager;
    private TitlePageIndicator mTPIndicator;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.preperation);

	mContext = getApplicationContext();
	db = ProxyRecipeProvider.getInstance(mContext);
	Bundle bundle = getIntent().getExtras();
	String recipeName = bundle.getString(DashboardActivity.ACTIONBAR_TITLE);
	recipe = db.getRecipe(recipeName);

	mFPAdapter = new mFPAdapter(getSupportFragmentManager());
	mViewPager = (ViewPager) findViewById(R.id.prep_viewpager);
	mViewPager.setAdapter(mFPAdapter);
	mTPIndicator = (TitlePageIndicator) findViewById(R.id.prep_indicator);
	mTPIndicator.setViewPager(mViewPager, 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case android.R.id.home:
	    finish();
	    break;
	}
	return super.onOptionsItemSelected(item);
    }

    private class mFPAdapter extends FragmentPagerAdapter implements
	    TitleProvider {

	public mFPAdapter(FragmentManager fm) {
	    super(fm);
	}

	@Override
	public Fragment getItem(int position) {
	    if (position == 0) {
		return new IngredientsFragment();
	    } else if (position == 1) {
		return new PreperationFragment();
	    }
	    return null;
	}

	@Override
	public int getCount() {
	    return NUM_ITEMS;
	}

	@Override
	public String getTitle(int position) {
	    if (position == 0) {
		return mContext.getResources().getString(
			R.string.ind_ingredients);
	    } else if (position == 1) {
		return mContext.getResources().getString(
			R.string.ind_preperation);
	    } else {
		return null;
	    }
	}
    }

    private class IngredientsFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
	    View v = inflater.inflate(R.layout.ingredients, container, false);
	    // TODO load ingredients
	    // add button to change protions, add logic to do so

	    double[] anzahl = recipe.getQuantities();
	    String[] einheit = recipe.getUnits();
	    String[] zutat = recipe.getIngredients();

	    String[] ingredients = new String[anzahl.length];

	    for (int i = 0; i < ingredients.length; i++) {
		ingredients[i] = anzahl[i] + einheit[i] + " " + zutat[i];
	    }

	    ListView mListView = (ListView) v
		    .findViewById(R.id.ingredients_listview);

	    mListView.setAdapter(new ArrayAdapter<String>(mContext,
		    R.layout.listitem, ingredients));

	    return v;
	}
    }

    private class PreperationFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
	    View v = inflater.inflate(R.layout.prepdetails, container, false);
	    TextView mTextView = (TextView) v.findViewById(R.id.prep_textview);
	    mTextView.setText(recipe.getPreperation());
	    return v;
	}
    }
}
