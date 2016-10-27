package com.aibang.aipolis.view;

import android.content.Context;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aibang.aipolis.R;

import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreUIHandler;

/**
 * 加载更多 底部视图
 * Created by zcf on 2016/5/10.
 */
public class ISLoadMoreFooterView extends RelativeLayout implements LoadMoreUIHandler {
    private ContentLoadingProgressBar mProgressBar;
    private TextView mTextView;

    public ISLoadMoreFooterView(Context context) {
        this(context, null);
    }

    public ISLoadMoreFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ISLoadMoreFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupViews();
    }

    private void setupViews() {
       LayoutInflater.from(getContext()).inflate(R.layout.load_more_foot_view, this);
        mTextView = (TextView) findViewById(R.id.id_tv);
        mProgressBar = (ContentLoadingProgressBar) findViewById(R.id.id_progressbar);
    }


    @Override
    public void onLoading(LoadMoreContainer container) {
        setVisibility(VISIBLE);
        mTextView.setText(R.string.loading);

    }

    @Override
    public void onLoadFinish(LoadMoreContainer container, boolean empty, boolean hasMore) {
        if (!hasMore) {
            setVisibility(VISIBLE);
            if (empty) {
                Toast.makeText(getContext(), R.string.no_comment, Toast.LENGTH_SHORT).show();

                setVisibility(INVISIBLE);
            } else {
                Toast.makeText(getContext(), R.string.no_more, Toast.LENGTH_SHORT).show();
                setVisibility(INVISIBLE);
            }
        } else {
            setVisibility(INVISIBLE);
        }

    }

    @Override
    public void onWaitToLoadMore(LoadMoreContainer container) {
        setVisibility(VISIBLE);
        mTextView.setText(R.string.loading);
    }
}
