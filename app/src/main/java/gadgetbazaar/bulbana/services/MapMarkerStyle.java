package gadgetbazaar.bulbana.services;



import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("Firma")
public class MapMarkerStyle extends ParseObject {

    //adı al
    public String getFirmaAdi(){
        return getString("firmaAdi");
    }
    public void setFirmaAdi(String value){
        put("firmaAdi", value);
    }

    public String getFirmaTelefon(){
        return getString("telefon");
    }

    public void setFirmaTelefon(String value){
        put("telefon", value);
    }

    public String getFirmaAdres(){
        return getString("adres");
    }

    public void setFirmaAdres(String value){
        put("adres", value);
    }


   /* public com.parse.ParseFile getLogo(){
        return getParseFile("logo");
    }
    public void setLogo(ParseFile value){
        put("logo", value);
    }
*/

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public void setLocation(ParseGeoPoint value) {
        put("location", value);
    }




    public static ParseQuery<MapMarkerStyle> getQuery(){
        return ParseQuery.getQuery(MapMarkerStyle.class);
    }

}
