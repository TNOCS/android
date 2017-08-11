package org.owntracks.android.db;

import org.greenrobot.greendao.annotation.*;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import android.location.Location;
// KEEP INCLUDES END
/**
 * Entity mapped to table "DAY".
 */
@Entity
public class Day {

    @Id
    private Long id;

    @NotNull
    private String description;
    private java.util.Date date;

    @Generated(hash = 1925857483)
    public Day(Long id, @NotNull String description, java.util.Date date) {
        this.id = id;
        this.description = description;
        this.date = date;
    }

    @Generated(hash = 866989762)
    public Day() {
    }

    public Day(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public String getDescription() {
        return description;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setDescription(@NotNull String description) {
        this.description = description;
    }

    public java.util.Date getDate() {
        return date;
    }

    public void setDate(java.util.Date date) {
        this.date = date;
    }


    public void setDefaults() {
    }
}
