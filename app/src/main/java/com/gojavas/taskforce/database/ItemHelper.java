package com.gojavas.taskforce.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gojavas.taskforce.entity.ItemEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by GJS280 on 14/5/2015.
 */
public class ItemHelper {

    private static ItemHelper instance = null;

    public static ItemHelper getInstance() {
        if(instance == null) {
            instance = new ItemHelper();
        }
        return instance;
    }

    /**
     * Get all items of particular docket
     * @return
     */
    public ArrayList<ItemEntity> getItems(String drs_docket) {
        ArrayList<ItemEntity> arrayList = new ArrayList<>();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.ITEM_TABLE_NAME + " where " + DatabaseHelper.DRS_DOCKET + " = ? ", new String[] {drs_docket});
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                ItemEntity item = getItemEntity(cursor);
                arrayList.add(item);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return arrayList;
    }

    /**
     * Get all items of particular docket
     * @return
     */
    public ArrayList<ItemEntity> getExchangeItems(String drs_docket, String docket_no) {
        ArrayList<ItemEntity> arrayList = new ArrayList<>();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.ITEM_TABLE_NAME + " where " + DatabaseHelper.DRS_DOCKET + " = ? AND " + DatabaseHelper.DOCKET_NO + " = ? ", new String[] {drs_docket, docket_no});
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                ItemEntity item = getItemEntity(cursor);
                arrayList.add(item);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return arrayList;
    }

    /**
     * Update item if exist otherwise insert new item
     * @param item
     */
    public void insertOrUpdate(ItemEntity item) {
//        String drs_docket = item.getdrs_docket();
//        String skuId = item.getsku_id();
//        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
//        Cursor cursor =  db.rawQuery( "select " + DatabaseHelper.DRS_DOCKET + " from  " + DatabaseHelper.ITEM_TABLE_NAME + " where " + DatabaseHelper.DRS_DOCKET + " = ? AND " + DatabaseHelper.SKU_ID + " = ? ", new String[] {drs_docket, skuId});
//        if(cursor.getCount() > 0) {
//            // Update drs
//            updateItem(item);
//        } else {
//            // Insert drs
            insertItem(item);
//        }
    }

    /**
     * Insert new item
     * @param item
     * @return
     */
    public long insertItem(ItemEntity item){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        ContentValues contentValues = getContentValues(item);
        long rowid = db.insert(DatabaseHelper.ITEM_TABLE_NAME, null, contentValues);
        return rowid;
    }

    /**
     * Update item
     * @param item
     */
    private void updateItem(ItemEntity item) {
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        ContentValues contentValues = getContentValuesUpdate(item);
        db.update(DatabaseHelper.ITEM_TABLE_NAME, contentValues, DatabaseHelper.DRS_DOCKET + " = ? AND " + DatabaseHelper.SKU_ID + " = ?", new String[]{item.getdrs_docket(), item.getsku_id()});
    }

    /**
     * Get item object
     * @param cursor
     * @return
     */
    private ItemEntity getItemEntity(Cursor cursor) {
        String sr = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SR));
        String drs_no = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRS_NO));
        String docket_no = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DOCKET_NO));
        String drs_docket = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRS_DOCKET));
        String sku_id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SKU_ID));
        String sku_description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SKU_DESCRIPTION));
        String sku_cost = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SKU_COST));
        String quantity = cursor.getString(cursor.getColumnIndex(DatabaseHelper.QUANTITY));
        String status = cursor.getString(cursor.getColumnIndex(DatabaseHelper.STATUS));

        ItemEntity item = new ItemEntity();
        item.setsr(sr);
        item.setdrs_no(drs_no);
        item.setdocket_no(docket_no);
        item.setdrs_docket(drs_docket);
        item.setsku_id(sku_id);
        item.setsku_description(sku_description);
        item.setsku_cost(sku_cost);
        item.setquantity(quantity);
        item.setStatus(status);


        return item;
    }

    private ContentValues getContentValues(ItemEntity item) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.SR, item.getsr());
        contentValues.put(DatabaseHelper.DRS_NO, item.getdrs_no());
        contentValues.put(DatabaseHelper.DOCKET_NO, item.getdocket_no());
        contentValues.put(DatabaseHelper.DRS_DOCKET, item.getdrs_docket());
        contentValues.put(DatabaseHelper.SKU_ID, item.getsku_id());
        contentValues.put(DatabaseHelper.SKU_DESCRIPTION, item.getsku_description());
        contentValues.put(DatabaseHelper.SKU_COST, item.getsku_cost());
        contentValues.put(DatabaseHelper.QUANTITY, item.getquantity());
        contentValues.put(DatabaseHelper.STATUS, "0");

        return contentValues;
    }


    private ContentValues getContentValuesUpdate(ItemEntity item) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.SR, item.getsr());
//        contentValues.put(DatabaseHelper.DRS_NO, item.getdrs_no());
//        contentValues.put(DatabaseHelper.DOCKET_NO, item.getdocket_no());
//        contentValues.put(DatabaseHelper.DRS_DOCKET, item.getdrs_docket());
        contentValues.put(DatabaseHelper.SKU_ID, item.getsku_id());
        contentValues.put(DatabaseHelper.SKU_DESCRIPTION, item.getsku_description());
        contentValues.put(DatabaseHelper.STATUS, item.getStatus());
        contentValues.put(DatabaseHelper.SKU_COST, item.getsku_cost());
        contentValues.put(DatabaseHelper.QUANTITY, item.getquantity());

        return contentValues;
    }

    public void statusUpdate(StringBuilder yes_array, StringBuilder no_array, String drs_docket) {
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        if (yes_array.length()>0){
            String query ="UPDATE item SET status = '1' WHERE " + DatabaseHelper.SR + " IN ('"+ yes_array +") AND " + DatabaseHelper.DRS_DOCKET+ " = '"+drs_docket+"'";
            db.execSQL(query);
        }
        if (no_array.length()>0){
            String  query1 ="UPDATE item SET status = '0' WHERE " + DatabaseHelper.SR + " IN ('"+ no_array +") AND " + DatabaseHelper.DRS_DOCKET+ " = '"+drs_docket+"'";
            db.execSQL(query1);
        }
    }

    /**
     * Update SKU item status
     * @param drs_docket
     */
    public void updateSkuItemStatus(String drs_docket, String status) {
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.STATUS, status);
        db.update(DatabaseHelper.ITEM_TABLE_NAME, contentValues, DatabaseHelper.DRS_DOCKET + " = ? ", new String[] {drs_docket});
    }

    /**
     * Get cost sum of selected items
     * @param drs_docket
     * @return
     */
    public String skuItemsSum(String drs_docket) {
//        SELECT SUM(sku_cost) FROM item where docket_no = '9343925941' AND status = '1'
        String amount = "";
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery("SELECT SUM(" + DatabaseHelper.SKU_COST + ") FROM " + DatabaseHelper.ITEM_TABLE_NAME + " where " + DatabaseHelper.DRS_DOCKET + " = ? AND " + DatabaseHelper.STATUS + " = ? ", new String[] {drs_docket, "1"});
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            amount = cursor.getString(0);
        }
        cursor.close();
        return amount;
    }

    /**
     * Get all items of particular docket in JSonArray
     * @return
     */
    public JSONArray getItemsJsonArray(String drs_docket) {
        JSONArray jsonArray=new JSONArray();

        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery("select * from  " + DatabaseHelper.ITEM_TABLE_NAME + " where " + DatabaseHelper.DRS_DOCKET + " = ? ", new String[]{drs_docket});
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                JSONObject jsonObject = getItemJsonObject(cursor);
                jsonArray.put(jsonObject);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return jsonArray;
    }



    /**
     * Get item object
     * @param cursor
     * @return
     */
    private JSONObject getItemJsonObject(Cursor cursor) {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(DatabaseHelper.SR, cursor.getString(cursor.getColumnIndex(DatabaseHelper.SR)));
            jsonObject.put(DatabaseHelper.DRS_NO, cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRS_NO)));
            jsonObject.put(DatabaseHelper.DOCKET_NO, cursor.getString(cursor.getColumnIndex(DatabaseHelper.DOCKET_NO)));
            jsonObject.put(DatabaseHelper.DRS_DOCKET, cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRS_DOCKET)));
            jsonObject.put(DatabaseHelper.SKU_ID, cursor.getString(cursor.getColumnIndex(DatabaseHelper.SKU_ID)));
            jsonObject.put(DatabaseHelper.SKU_DESCRIPTION,  cursor.getString(cursor.getColumnIndex(DatabaseHelper.SKU_DESCRIPTION)));
            jsonObject.put(DatabaseHelper.SKU_COST, cursor.getString(cursor.getColumnIndex(DatabaseHelper.SKU_COST)));
            jsonObject.put(DatabaseHelper.QUANTITY,  cursor.getString(cursor.getColumnIndex(DatabaseHelper.QUANTITY)));
            jsonObject.put(DatabaseHelper.STATUS, cursor.getString(cursor.getColumnIndex(DatabaseHelper.STATUS)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**
     * Get delivery detail
     * @return
     */
    public boolean itemExist(String drs_docket, String skuId) {
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select " + DatabaseHelper.DRS_DOCKET + " from  " + DatabaseHelper.ITEM_TABLE_NAME + " where " + DatabaseHelper.DRS_DOCKET + " = ? AND " + DatabaseHelper.SKU_ID + " = ? ", new String[] {drs_docket, skuId});
        if(cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    /**
     * Check all the items of this docket are pulled or not
     * @param drs_docket
     * @param totalItems
     * @return
     */
    public boolean allItemsPulled(String drs_docket, String totalItems) {
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select " + DatabaseHelper.DRS_DOCKET + " from  " + DatabaseHelper.ITEM_TABLE_NAME + " where " + DatabaseHelper.DRS_DOCKET + " = ? ", new String[] {drs_docket});
        if(cursor.getCount() > 0) {
            if(Integer.parseInt(totalItems) == cursor.getCount()) {
                cursor.close();
                return true;
            }
        }
        cursor.close();
        return false;
    }

    /**
     * Delete Item table
     * @return
     */
    public boolean deleteItemTable() {
        int doneDelete = 0;
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        doneDelete = db.delete(DatabaseHelper.ITEM_TABLE_NAME, null , null);
        db.close();
        return doneDelete > 0;
    }
}