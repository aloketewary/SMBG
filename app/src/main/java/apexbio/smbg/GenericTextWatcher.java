package apexbio.smbg;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by A1302 on 2015/8/14.
 */
public class GenericTextWatcher implements TextWatcher {
    private static String strRpwd = null;
    private static String strRpwdconfirm = null;
    private int editStart;
    private int editEnd;
    public View view;
    public GenericTextWatcher(View view) {
        this.view = view;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        String text = s.toString();
        switch(view.getId()){
            // R - Register
            case R.id.edRaccount:
                EditText edRaccount = (EditText)view.findViewById(R.id.edRaccount);
                editStart = edRaccount.getSelectionStart();
                editEnd = edRaccount.getSelectionEnd();
                if(text.length() < 6 && !text.isEmpty()){
                    edRaccount.setError(Html.fromHtml("<font color='blue'>帳號長度須在6~15個字元之間</font>"));
                }else if(text.length() > 15){
                    Toast.makeText(view.getContext(), "帳號長度須在6~15個字元之間", Toast.LENGTH_SHORT).show();
                    //edRaccount.setError(Html.fromHtml("<font color='blue'>帳號長度須在7~15個字元之間</font>"));
                    s.delete(editStart-1, editEnd);
                    int tempSelection = editStart;
                    edRaccount.setText(s);
                    edRaccount.setSelection(tempSelection);
                }
                break;
            case R.id.edRpwd:
                EditText edRpwd = (EditText)view.findViewById(R.id.edRpwd);
                strRpwd = edRpwd.getText().toString();
                editStart = edRpwd.getSelectionStart();
                editEnd = edRpwd.getSelectionEnd();
                if(text.length() < 6 && !text.isEmpty()){
                    edRpwd.setError(Html.fromHtml("<font color='blue'>帳號長度須在6~15個字元之間</font>"));
                }else if(text.length() > 15){
                    Toast.makeText(view.getContext(), "帳號長度須在6~15個字元之間", Toast.LENGTH_SHORT).show();
                    s.delete(editStart-1, editEnd);
                    int tempSelection = editStart;
                    edRpwd.setText(s);
                    edRpwd.setSelection(tempSelection);
                }
                break;
            case R.id.edRpwdconfirm:
                EditText edRpwdconfirm = (EditText)view.findViewById(R.id.edRpwdconfirm);
                if(edRpwdconfirm != null && strRpwd != null){
                    strRpwdconfirm = edRpwdconfirm.getText().toString();
                    if(!strRpwd.equals(strRpwdconfirm)){
                        edRpwdconfirm.setError(Html.fromHtml("<font color='blue'>密碼確認不正確</font>"));
                    }
                }
                break;
            case R.id.edRname:
                EditText edRname = (EditText)view.findViewById(R.id.edRname);
                editStart = edRname.getSelectionStart();
                editEnd = edRname.getSelectionEnd();
                if(text.length() > 20 && !text.isEmpty()){
                    Toast.makeText(view.getContext(), "名字字串不得超過 20 字元", Toast.LENGTH_SHORT).show();
                    //edRaccount.setError(Html.fromHtml("<font color='blue'>帳號長度須在7~15個字元之間</font>"));
                    s.delete(editStart-1, editEnd);
                    int tempSelection = editStart;
                    edRname.setText(s);
                    edRname.setSelection(tempSelection);
                }
                break;
            case R.id.edRheight:
                EditText edRheight = (EditText)view.findViewById(R.id.edRheight);
                if(text.isEmpty()){
                    edRheight.setError(Html.fromHtml("<font color='blue'>請輸入身高.</font>"));
                }
                break;
            case R.id.edRweight:
                EditText edRweight = (EditText)view.findViewById(R.id.edRweight);
                if(text.isEmpty()){
                    edRweight.setError(Html.fromHtml("<font color='blue'>請輸入體重.</font>"));
                }
                break;
            case R.id.edRemail:
                EditText edRemail = (EditText)view.findViewById(R.id.edRemail);
                if(!isValidEmail(s.toString())){
                    edRemail.setError(Html.fromHtml("<font color='blue'>請輸入正確的E-mail.</font>"));
                }
                break;
            // K - Keyin
            case R.id.edKmetername:
                EditText edKmetername = (EditText)view.findViewById(R.id.edKmetername);
                editStart = edKmetername.getSelectionStart();
                editEnd = edKmetername.getSelectionEnd();
                if(text.length() > 20 && !text.isEmpty()){
                    Toast.makeText(view.getContext(), "機器名字字串不得超過 20 字元", Toast.LENGTH_SHORT).show();
                    s.delete(editStart-1, editEnd);
                    int tempSelection = editStart;
                    edKmetername.setText(s);
                    edKmetername.setSelection(tempSelection);
                }
                break;
            case R.id.edKdate:
                EditText edKdate = (EditText)view.findViewById(R.id.edKdate);
                if(text.isEmpty()){
                    edKdate.setError(Html.fromHtml("<font color='blue'>請輸入日期.</font>"));
                }
                break;
            case R.id.edKtime:
                EditText edKtime = (EditText)view.findViewById(R.id.edKtime);
                if(text.isEmpty()){
                    edKtime.setError(Html.fromHtml("<font color='blue'>請輸入時間.</font>"));
                }
                break;
            case R.id.edKvalue:
                EditText edKvalue = (EditText)view.findViewById(R.id.edKvalue);
                if(text.isEmpty()){
                    edKvalue.setError(Html.fromHtml("<font color='blue'>請輸入血糖數值.</font>"));
                }
                break;
            case R.id.edKheight:
                EditText edKheight = (EditText)view.findViewById(R.id.edKheight);
                if(text.isEmpty()){
                    edKheight.setError(Html.fromHtml("<font color='blue'>請輸入身高.</font>"));
                }
                break;
            case R.id.edKweight:
                EditText edKweight = (EditText)view.findViewById(R.id.edKweight);
                if(text.isEmpty()){
                    edKweight.setError(Html.fromHtml("<font color='blue'>請輸入體重.</font>"));
                }
                break;
            case R.id.edKnote:
                //
                break;
        }
    }

    public final static boolean isValidEmail(String target) {
        if (target == null) {
            return false;
        } else {
            //android Regex to check the email address Validation
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
