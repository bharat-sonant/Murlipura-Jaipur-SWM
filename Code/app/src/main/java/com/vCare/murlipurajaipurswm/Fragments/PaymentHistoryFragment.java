package com.vCare.murlipurajaipurswm.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.vCare.murlipurajaipurswm.R;

public class PaymentHistoryFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    private static WebView webViewContainer;
    @SuppressLint("StaticFieldLeak")
    private static ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_history, container, false);

        webViewContainer = view.findViewById(R.id.webViewContainer);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        webViewContainer.setWebViewClient(new MyWebViewClient());

        webViewContainer.getSettings().setJavaScriptEnabled(true);
        webViewContainer.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webViewContainer.getSettings().setDomStorageEnabled(true);
//        webViewContainer.loadUrl("https://wevois-qa-ccavenue.web.app/");
        webViewContainer.loadUrl("https://ccavenuemurlipura.web.app");
        return view;
    }

    private static class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
            webViewContainer.setVisibility(View.VISIBLE);
        }
    }
}