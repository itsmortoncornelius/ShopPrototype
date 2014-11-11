package de.handler.mobile.android.shopprototype.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import de.handler.mobile.android.shopprototype.R;

/**
 * Displays Web Content
 */
@EFragment(R.layout.fragment_web)
public class WebFragment extends Fragment {

    public static final String URI = "activity_web_uri";
    public static final String HTTP_CONTENT = "http_content";
    public static final String COOKIE = "cart_cookie";

    @ViewById(R.id.fragment_web_webview)
    WebView webView;

    @ViewById(R.id.fragment_web_progress_bar)
    ProgressBar progressBar;


    //TODO after redirect 500 from fairmondo page --> cookie not set correctly?
    @AfterViews
    public void init() {

        String http = getArguments().getString(HTTP_CONTENT);
        String uri = getArguments().getString(URI);
        String cookie = getArguments().getString(COOKIE);


        if (http != null) {
            webView.loadData(http, "text/html; charset=UTF-8", null);

        } else if (uri != null && !uri.equals("")) {

            progressBar.setVisibility(View.VISIBLE);
            if (cookie != null) {
                webView.setWebViewClient(new RedirectWebViewClient(false));
                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    CookieSyncManager.createInstance(getActivity());
                }

                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.setCookie(uri, "cart=" + cookie);

                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    CookieSyncManager.getInstance().sync();
                }

            } else {
                webView.setWebViewClient(new RedirectWebViewClient());
            }

            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webView.loadUrl(uri);
        }
    }

    private class RedirectWebViewClient extends WebViewClient {

        boolean handleKeyEvent = true;

        RedirectWebViewClient (boolean handleKeyEvent) {
            this.handleKeyEvent = handleKeyEvent;
        }
        RedirectWebViewClient() {}

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            super.shouldOverrideUrlLoading(view, url);
            Log.d(getClass().getCanonicalName(), "redirected to: " + url);

            if (handleKeyEvent) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }

            return handleKeyEvent;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }
    }


}