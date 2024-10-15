package com.sparkleside;
import android.graphics.Typeface;
import androidx.activity.EdgeToEdge;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.MarginLayoutParamsCompat;
import 	android.view.ViewGroup.MarginLayoutParams;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.sparkleside.databinding.ActivityMainBinding;
import com.sparkleside.component.ExpandableLayout;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

public class MainActivity extends AppCompatActivity {
	
	  private ActivityMainBinding binding;
	  private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);

        super.onCreate(savedInstanceState);
		    binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
		    setSupportActionBar(binding.toolbar);
            binding.toolbox.setExpansion(false);
            binding.toolbox.setDuration(200);
            binding.toolbox.setOrientatin(ExpandableLayout.VERTICAL);
		    binding.fab.setOnClickListener(v ->
          Toast.makeText(MainActivity.this, "ComingSoon", Toast.LENGTH_SHORT).show()
        ); 
            binding.settings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(intent);
            
           } );
        binding.editor.setTypefaceText(Typeface.createFromAsset(getAssets(), "fonts/jetbrainsmono"+".ttf"));
        
        var scheme = binding.editor.getColorScheme();
        scheme.setColor(EditorColorScheme.WHOLE_BACKGROUND, 0xFF070707);
        scheme.setColor(EditorColorScheme.CURRENT_LINE , 0xFF070707);
        scheme.setColor(EditorColorScheme.LINE_NUMBER_PANEL , 0xFF070707);
        scheme.setColor(EditorColorScheme.LINE_NUMBER_BACKGROUND , 0xFF070707);
        scheme.setColor(EditorColorScheme.KEYWORD , 0xFF62DE8A);
        scheme.setColor(EditorColorScheme.FUNCTION_NAME , 0xFF62DE8A);
        scheme.setColor(EditorColorScheme.TEXT_NORMAL , 0XFFA2D2A9);
        scheme.setColor(EditorColorScheme.OPERATOR , 0xFFDDE5DB);

  ViewCompat.setOnApplyWindowInsetsListener(binding.fab, (v, windowInsets) -> {
  Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
  MarginLayoutParams mlp = (MarginLayoutParams) v.getLayoutParams();
  //mlp.leftMargin = insets.left;
  mlp.bottomMargin = insets.bottom;
//  mlp.rightMargin = insets.right;
  v.setLayoutParams(mlp);
    return WindowInsetsCompat.CONSUMED;
});
        
        
        
        
    }
        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_more) {
            	if (!binding.toolbox.isExpanded()) {
			binding.toolbox.expand();
		}
		else {
			binding.toolbox.collapse();
		}
            return true;
        }
        if (id == R.id.menu_undo) {
            binding.editor.undo();
            return true;
        }
        if (id == R.id.menu_redo) {
            binding.editor.redo();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }
}