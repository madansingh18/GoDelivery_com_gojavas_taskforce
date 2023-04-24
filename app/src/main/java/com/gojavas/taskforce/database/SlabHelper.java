package com.gojavas.taskforce.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gojavas.taskforce.entity.SlabEntity;

import java.util.ArrayList;

/**
 * Created by GJS280 on 14/5/2015.
 */
public class SlabHelper {

    private static SlabHelper instance = null;

    public static SlabHelper getInstance() {
        if(instance == null) {
            instance = new SlabHelper();
        }
        return instance;
    }

    /**
     * Get all slabs of particular docket
     * @return
     */
    public ArrayList<SlabEntity> getSlabs(String docketNo) {
        ArrayList<SlabEntity> arrayList = new ArrayList<>();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.SLAB_TABLE_NAME + " where " + DatabaseHelper.DOCKET_NO + " = ? ", new String[] {docketNo});
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                SlabEntity slab = getSlabEntity(cursor);
                arrayList.add(slab);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return arrayList;
    }

    /**
     * Update slab if exist otherwise insert new slab
     * @param slab
     */
    public void insertOrUpdate(SlabEntity slab) {
        String sr = slab.getsr();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.SLAB_TABLE_NAME + " where " + DatabaseHelper.SR + " = ? ", new String[] {sr});
        if(cursor.getCount() > 0) {
            // Update drs
            updateSlab(slab);
        } else {
            // Insert drs
            insertSlab(slab);
        }
    }

    /**
     * Insert new slab
     * @param slab
     * @return
     */
    public long insertSlab(SlabEntity slab){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        ContentValues contentValues = getContentValues(slab);
        long rowid = db.insert(DatabaseHelper.SLAB_TABLE_NAME, null, contentValues);

        return rowid;
    }

    /**
     * Update drs
     * @param slab
     */
    private void updateSlab(SlabEntity slab) {
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        ContentValues contentValues = getContentValues(slab);
        db.update(DatabaseHelper.ITEM_TABLE_NAME, contentValues, DatabaseHelper.SR + " = ? ", new String[] {slab.getsr()});
    }

    /**
     * Get slab object
     * @param cursor
     * @return
     */
    private SlabEntity getSlabEntity(Cursor cursor) {
        String sr = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SR));
        String drs_no = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRS_NO));
        String docket_no = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DOCKET_NO));
        String slabfrom_to = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SLABFROM_TO));
        String slab_rate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SLAB_RATE));

        SlabEntity slab = new SlabEntity();
        slab.setsr(sr);
        slab.setdrs_no(drs_no);
        slab.setdocket_no(docket_no);
        slab.setslabfrom_to(slabfrom_to);
        slab.setslab_rate(slab_rate);

        return slab;
    }

    private ContentValues getContentValues(SlabEntity slab) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.SR, slab.getsr());
        contentValues.put(DatabaseHelper.DRS_NO, slab.getdrs_no());
        contentValues.put(DatabaseHelper.DOCKET_NO, slab.getdocket_no());
        contentValues.put(DatabaseHelper.SLABFROM_TO, slab.getslabfrom_to());
        contentValues.put(DatabaseHelper.SLAB_RATE, slab.getslab_rate());

        return contentValues;
    }

    /**
     * Delete Slab table
     * @return
     */
    public boolean deleteSlabTable() {
        int doneDelete = 0;
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        doneDelete = db.delete(DatabaseHelper.SLAB_TABLE_NAME, null , null);
        db.close();
        return doneDelete > 0;
    }
}
