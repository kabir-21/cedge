package sample;


import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Account{
    private StringProperty merchantName;
    private StringProperty accountId;
    private StringProperty date;
    private StringProperty accountType;
    private StringProperty branchID;

    public String getAccountId() {
        return accountId.get();
    }

    public String getAccountType() {
        return accountType.get();
    }

    public String getBranchID() {
        return branchID.get();
    }

    public String getDate() {
        return date.get();
    }

    public String getMerchantName() {
        return merchantName.get();
    }

    public Account(String accountId, String date, String accType, String branch, String merch){
        setAccountId(accountId);
        setAccountType(accType);
        setDate(date);
        setBranchID(branch);
        setMerchantName(merch);
    }

    public StringProperty merchantNameProperty() {
        if(merchantName==null)
            merchantName = new SimpleStringProperty();
        return merchantName;
    }

    public StringProperty accountIdProperty() {
        if(accountId==null)
            accountId = new SimpleStringProperty();
        return accountId;
    }

    public StringProperty accountTypeProperty() {
        if(accountType==null)
            accountType = new SimpleStringProperty();
        return accountType;
    }

    public StringProperty dateProperty() {
        if (date==null)
            date = new SimpleStringProperty();
        return date;
    }

    public StringProperty branchIDProperty() {
        if(branchID==null)
            branchID = new SimpleStringProperty();
        return branchID;
    }

    public void setAccountType(String accountType) {
        accountTypeProperty().setValue(accountType);
    }
    public void setDate(String date) {
        dateProperty().setValue(date);
    }
    public void setAccountId(String accountId) {
        accountIdProperty().setValue(accountId);
    }
    public void setMerchantName(String merchantName) {
        merchantNameProperty().setValue(merchantName);
    }
    public void setBranchID(String branchID) {
        branchIDProperty().setValue(branchID);
    }
}
