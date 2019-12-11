package com.breiter.seatswapper.tool;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.breiter.seatswapper.R;
import com.breiter.seatswapper.adapter.MailAdapter;
import com.breiter.seatswapper.model.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Iterator;
import java.util.List;

import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;

public class SwipeController extends ItemTouchHelper.SimpleCallback {
    private Context context;
    private Drawable icon;
    private final ColorDrawable background;
    private MailAdapter mailAdapter;
    private List<Message> mailList;


    public SwipeController(Context context, MailAdapter mailAdapter, List<Message> mailList) {
        super(0, LEFT);
        this.context = context;
        icon = context.getDrawable(R.drawable.ic_delete);
        background = new ColorDrawable(Color.RED);
        this.mailAdapter = mailAdapter;
        this.mailList = mailList;

    }


    //Not swipable position for unread messages
    @Override
    public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

        Message messageToBeRemoved = mailList.get(viewHolder.getAdapterPosition());


        if (messageToBeRemoved.getType().equals("pending request")) {

            if (!messageToBeRemoved.getRequester().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))

                return 0;


        } else {

            if (!messageToBeRemoved.isIsread() && messageToBeRemoved.getRequester().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))

                return 0;


        }

    return super.getSwipeDirs(recyclerView, viewHolder);

}

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder
            viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        int position = viewHolder.getAdapterPosition();

        mailAdapter.deleteMessage(position);

    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView
            recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;

        int backgroundCornerOffset = 20;

        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();


        if (dX < 0) {
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());

        } else {
            background.setBounds(0, 0, 0, 0);
            icon.setBounds(0, 0, 0, 0);


        }
        background.draw(c);

        icon.draw(c);

    }
}



