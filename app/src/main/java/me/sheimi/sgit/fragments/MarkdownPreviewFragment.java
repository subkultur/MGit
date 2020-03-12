package me.sheimi.sgit.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yydcdut.markdown.MarkdownConfiguration;
import com.yydcdut.markdown.MarkdownProcessor;
import com.yydcdut.markdown.MarkdownTextView;
import com.yydcdut.markdown.callback.OnLinkClickCallback;
import com.yydcdut.markdown.callback.OnTodoClickCallback;
import com.yydcdut.markdown.loader.MDImageLoader;
import com.yydcdut.markdown.syntax.text.TextFactory;
import com.yydcdut.markdown.theme.ThemeSunburst;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import me.sheimi.android.activities.SheimiFragmentActivity;
import me.sheimi.sgit.R;
import me.sheimi.sgit.database.models.Repo;

public class MarkdownPreviewFragment extends BaseFragment {

    private Repo mRepo;
    private String mFile;
    private static final String FILE = "preview_file";

    public static MarkdownPreviewFragment newInstance(Repo mRepo, String file) {
        MarkdownPreviewFragment fragment = new MarkdownPreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Repo.TAG, mRepo);
        if (file != null) {
            bundle.putString(FILE, file);
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_preview, container, false);
        Bundle bundle = getArguments();
        mRepo = (Repo) bundle.getSerializable(Repo.TAG);
        if (mRepo == null) {
            return v;
        }
        mFile = bundle.getString(FILE);
        String content = "";
        try {
            content = FileUtils.readFileToString(new File(mFile));
        } catch (IOException e) {
            toast("Cannot read file");
        }

        MarkdownTextView markdownTextView = (MarkdownTextView) v.findViewById(R.id.txt_md_show);
        markdownTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        MDImageLoader mdImageLoader = new RepoMDImageLoader(this.getRawActivity(), mRepo);
        markdown(markdownTextView, content, mdImageLoader);
        return v;
    }

    private void toast(String msg) {
        Toast.makeText(this.getContext(), msg, Toast.LENGTH_LONG).show();
    }

    private void markdown(final TextView textView, String content, MDImageLoader imageLoader) {

        MarkdownConfiguration markdownConfiguration = new MarkdownConfiguration.Builder(this.getContext())
            .setDefaultImageSize(50, 50)
            .setBlockQuotesLineColor(0xff33b5e5)
            .setHeader1RelativeSize(1.6f)
            .setHeader2RelativeSize(1.5f)
            .setHeader3RelativeSize(1.4f)
            .setHeader4RelativeSize(1.3f)
            .setHeader5RelativeSize(1.2f)
            .setHeader6RelativeSize(1.1f)
            .setHorizontalRulesColor(0xff99cc00)
            .setCodeBgColor(0xffff4444)
            .setTodoColor(0xffaa66cc)
            .setTodoDoneColor(0xffff8800)
            .setUnOrderListColor(0xff00ddff)
            .setRxMDImageLoader(imageLoader)
            .setHorizontalRulesHeight(1)
            .setLinkFontColor(Color.BLUE)
            .showLinkUnderline(false)
            .setTheme(new ThemeSunburst())
            .setOnLinkClickCallback(new OnLinkClickCallback() {
                @Override
                public void onLinkClicked(View view, String link) {
                    toast(link);
                }
            })
            .setOnTodoClickCallback(new OnTodoClickCallback() {
                @Override
                public CharSequence onTodoClicked(View view, String line, int lineNumber) {
                    toast("line:" + line + "\n" + "line number:" + lineNumber);
                    return textView.getText();
                }
            })
            .build();

        //MarkdownConfiguration markdownConfiguration = new MarkdownConfiguration.Builder(this.getContext())
        //    .setRxMDImageLoader(imageLoader)
        //    .build();
        MarkdownProcessor processor = new MarkdownProcessor(this.getContext());
        processor.factory(TextFactory.create());
        processor.config(markdownConfiguration);
        textView.setText(processor.parse(content));
    }

    @Override
    public SheimiFragmentActivity.OnBackClickListener getOnBackClickListener() {
        return null;
    }

    @Override
    public void reset() {

    }

    class RepoMDImageLoader implements MDImageLoader {

        private Context mContext;
        private Repo mRepo;

        public RepoMDImageLoader(Context context, Repo mRepo) {
            this.mContext = context;
            this.mRepo = mRepo;
        }

        @Nullable
        @Override
        public byte[] loadSync(@NonNull String url) throws IOException {
            return new byte[0];
        }
    }
}
