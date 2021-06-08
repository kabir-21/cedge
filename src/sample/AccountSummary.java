package sample;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AccountSummary {
    private StringProperty openingDate;
    private IntegerProperty count;
    AccountSummary(String openingDate, int count){
        setCount(count);
        setOpeningDate(openingDate);
    }
    public String getOpeningDate() {
        return openingDate.get();
    }

    public int getCount() {
        return count.get();
    }

    public IntegerProperty countProperty() {
        if(count==null)
            count = new SimpleIntegerProperty();
        return count;
    }
    public StringProperty openingDateProperty() {
        if(openingDate==null)
            openingDate = new SimpleStringProperty();
        return openingDate;
    }

    public void setCount(int count) {
        countProperty().setValue(count);
    }

    public void setOpeningDate(String openingDate) {
        openingDateProperty().setValue(openingDate);
    }
}
