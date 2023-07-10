package nep.timeline.tgar.structs;

import java.util.ArrayList;

public class DeletedMessageInfo {
    private final int selectedAccount;
    private final ArrayList<Integer> messageIds;

    public DeletedMessageInfo(int selectedAccount, ArrayList<Integer> messageIds)
    {
        this.selectedAccount = selectedAccount;
        this.messageIds =  messageIds;
    }

    public int getSelectedAccount() {
        return this.selectedAccount;
    }

    public ArrayList<Integer> getMessageIds() {
        return this.messageIds;
    }

    public void insertMessageIds(ArrayList<Integer> messageIds) {
        this.messageIds.addAll(messageIds);
    }

    public void insertMessageId(Integer messageId) {
        this.messageIds.add(messageId);
    }
}
