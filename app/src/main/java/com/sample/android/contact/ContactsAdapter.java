package com.sample.android.contact;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sample.android.contact.Utils.deAccent;
import static com.sample.android.contact.Utils.getTypeValue;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private List<Contact> mContacts;
    private RecyclerView mRecyclerView;
    private RecyclerView.SmoothScroller mSmoothScroller;

    // State of the row that needs to show separator
    private static final int SECTIONED_STATE = 1;
    // State of the row that need not show separator
    private static final int REGULAR_STATE = 2;
    // Cache row states based on positions
    private int[] mSeparatorRowStates;
    private int[] mLineRowStates;

    ContactsAdapter(List<Contact> contacts) {
        mContacts = contacts;
        mSeparatorRowStates = new int[getItemCount()];
        mLineRowStates = new int[getItemCount()];
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.contact_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Contact contact = mContacts.get(position);

        holder.bind(contact, position);

        holder.detail.setOnClickListener(v -> {

            boolean expanded = contact.isExpanded();
            contact.setExpanded(!expanded);
            notifyItemChanged(position);

            if (contact.isExpanded()) {
                mSmoothScroller.setTargetPosition(position);
                new Handler().postDelayed(() ->
                        mRecyclerView.getLayoutManager().startSmoothScroll(mSmoothScroller), 100);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mContacts == null ? 0 : mContacts.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;

        mSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return 150f / displayMetrics.densityDpi;
            }
        };
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.detail)
        View detail;

        @BindView(R.id.contact_name)
        TextView contactNameView;

        @BindView(R.id.phone_number)
        TextView phoneNumberView;

        @BindView(R.id.phone_type)
        TextView phoneNumberType;

        @BindView(R.id.line_number)
        TextView lineNumber;

        @BindView(R.id.subItem)
        LinearLayout subItem;

        @BindView(R.id.image_text)
        TextView imageText;

        @BindView(R.id.separator)
        View separatorView;

        @BindView(R.id.separator_text)
        TextView separatorText;

        @BindView(R.id.line)
        View line;

        private ViewHolder(View root) {
            super(root);
            ButterKnife.bind(this, root);
        }

        private void bind(Contact contact, int position) {

            String name = contact.getName();
            List<PhoneNumber> numbers = contact.getPhoneNumbers();
            String number = numbers.size() == 1 ? numbers.get(0).getNumber() : "";

            contactNameView.setText(name);
            phoneNumberView.setText(number);
            if (numbers.size() == 1) {
                phoneNumberType.setVisibility(View.VISIBLE);
                lineNumber.setVisibility(View.INVISIBLE);
                phoneNumberType.setText(getTypeValue(numbers.get(0).getType()));
            } else {
                lineNumber.setVisibility(View.VISIBLE);
                phoneNumberType.setVisibility(View.INVISIBLE);
                lineNumber.setText(String.valueOf(numbers.size()));
            }

            String[] splitedName = name.split("\\s+");
            char c;
            int i;
            boolean noLetter = true;

            for (i = 0; i < splitedName.length; i++) {
                c = splitedName[i].toUpperCase().charAt(0);
                if (Character.isLetter(c)) {
                    imageText.setText(String.valueOf(c));
                    noLetter = false;
                    break;
                }
            }

            for (int j = i + 1; j < splitedName.length; j++) {
                c = splitedName[j].toUpperCase().charAt(0);
                if (Character.isLetter(c)) {
                    imageText.append("." + c);
                    break;
                }
            }

            if (noLetter) {
                imageText.setText("¿");
            }

            boolean showSeparator = false;

            // Show separator ?
            switch (mSeparatorRowStates[position]) {

                case SECTIONED_STATE:
                    showSeparator = true;
                    break;

                case REGULAR_STATE:
                    showSeparator = false;
                    break;

                default:

                    if (position == 0) {
                        showSeparator = true;
                    } else {
                        Contact previousContact = mContacts.get(position - 1);

                        String previousName = deAccent(previousContact.getName());
                        char[] previousNameArray = previousName.toUpperCase().toCharArray();
                        char[] nameArray = deAccent(name).toUpperCase().toCharArray();

                        if (Character.isLetter(nameArray[0]) &&
                                nameArray[0] != previousNameArray[0]) {
                            showSeparator = true;
                        }
                    }

                    // Cache it
                    mSeparatorRowStates[position] = showSeparator ? SECTIONED_STATE : REGULAR_STATE;

                    break;
            }

            if (showSeparator) {
                char ch = name.toUpperCase().charAt(0);
                if (Character.isLetter(ch)) {
                    separatorText.setText(name.toCharArray(), 0, 1);
                } else {
                    separatorText.setText("&");
                }
                separatorView.setVisibility(View.VISIBLE);
            } else {
                separatorView.setVisibility(View.GONE);
            }

            boolean showLine = true;

            // Show separator ?
            switch (mLineRowStates[position]) {

                case SECTIONED_STATE:
                    showLine = false;
                    break;

                case REGULAR_STATE:
                    showLine = true;
                    break;

                default:

                    if (position == mContacts.size() - 1) {
                        showLine = false;
                    } else {
                        Contact nextContact = mContacts.get(position + 1);

                        String nextName = deAccent(nextContact.getName());
                        char[] nextNameArray = nextName.toUpperCase().toCharArray();
                        char[] nameArray = deAccent(name).toUpperCase().toCharArray();

                        if ((Character.isLetter(nameArray[0]) && nameArray[0] != nextNameArray[0]) ||
                                (!Character.isLetter(nameArray[0]) && Character.isLetter(nextNameArray[0])
                                        && nameArray[0] != nextNameArray[0])) {
                            showLine = false;
                        }
                    }

                    // Cache it
                    mLineRowStates[position] = showLine ? REGULAR_STATE : SECTIONED_STATE;

                    break;
            }

            line.setVisibility(showLine ? View.VISIBLE : View.GONE);

            subItem.removeAllViews();

            if (numbers.size() == 1) {
                return;
            }

            boolean expanded = contact.isExpanded();
            subItem.setVisibility(expanded ? View.VISIBLE : View.GONE);
            Context context = mRecyclerView.getContext();

            for (int childPosition = 0; childPosition < numbers.size(); childPosition++) {

                PhoneNumber phoneNumber = numbers.get(childPosition);

                LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View childView = infalInflater.inflate(R.layout.child_item, null);
                childView.setOnClickListener(v -> {
                });

                TextView contactNumber = childView.findViewById(R.id.contact_number);
                TextView numberType = childView.findViewById(R.id.type);

                contactNumber.setText(phoneNumber.getNumber());
                numberType.setText(getTypeValue(phoneNumber.getType()));

                boolean lineFlag = true;

                if (position == mContacts.size() - 1) {
                    lineFlag = false;
                } else {
                    Contact nextContact = mContacts.get(position + 1);

                    String nextName = deAccent(nextContact.getName());
                    char[] nextNameArray = nextName.toUpperCase().toCharArray();
                    char[] nameArray = deAccent(mContacts.get(position).getName()).toUpperCase().toCharArray();

                    if ((Character.isLetter(nameArray[0]) || Character.isLetter(nextNameArray[0]))
                            && nameArray[0] != nextNameArray[0]) {
                        lineFlag = false;
                    }
                }

                View childLine = childView.findViewById(R.id.child_line);
                childLine.setVisibility(lineFlag ? View.VISIBLE : View.GONE);

                View childTopLine = childView.findViewById(R.id.child_top_line);
                childTopLine.setVisibility(lineFlag ? View.GONE : View.VISIBLE);

                View frameLayout = childView.findViewById(R.id.frameLayout);
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);

                View relativeLayout = childView.findViewById(R.id.relativeLayout);
                FrameLayout.LayoutParams rlp = new FrameLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);

                if (lineFlag) {
                    lp.setMarginStart((int) context.getResources().getDimension(R.dimen.dimen_frame_margin_default));
                    frameLayout.setLayoutParams(lp);

                    rlp.setMarginStart((int) context.getResources().getDimension(R.dimen.dimen_relative_margin_default));
                    relativeLayout.setLayoutParams(rlp);
                } else {

                    if (childPosition == 0) {
                        lp.setMarginStart((int) context.getResources().getDimension(R.dimen.dimen_frame_margin_default));
                        rlp.setMarginStart((int) context.getResources().getDimension(R.dimen.dimen_relative_margin_default));

                    } else {

                        lp.setMarginStart((int) context.getResources().getDimension(R.dimen.dimen_frame_margin));
                        rlp.setMarginStart(0);
                    }
                    frameLayout.setLayoutParams(lp);
                    relativeLayout.setLayoutParams(rlp);
                }

                subItem.addView(childView);
            }
        }
    }
}
