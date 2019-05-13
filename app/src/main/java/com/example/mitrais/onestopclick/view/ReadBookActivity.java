package com.example.mitrais.onestopclick.view;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.R;
import com.github.barteksc.pdfviewer.PDFView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReadBookActivity extends AppCompatActivity {
    private static final String TAG = "ReadBookActivity";
    private Uri bookUri;

    @BindView(R.id.pdf_view)
    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_book);
        ButterKnife.bind(this);

        if (getIntent() != null) {
            bookUri = Uri.parse(getIntent().getStringExtra(Constant.EXTRA_BOOK_URI));
            initPDF(bookUri);
        }
    }

    /**
     * @param uri book uri
     */
    private void initPDF(Uri uri) {
        pdfView.fromUri(Uri.parse("http://detective.gumer.info/anto/christie_24_2.pdf"))
                .swipeHorizontal(false)
                .enableSwipe(true)
                .enableDoubletap(true)
                .defaultPage(0)
                .invalidPageColor(R.color.white)
                .onError(e -> Log.e(TAG, "Failed to load PDF file: " + e.getMessage()))
                .load();
    }
}
