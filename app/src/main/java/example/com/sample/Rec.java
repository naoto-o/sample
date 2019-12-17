package example.com.sample;

public class Rec {

    long id;
    private String name;
    private String upDateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name=name;
    }

    public String getUpdateTime() {
        return upDateTime;
    }

    public void setUpdateTime(String upDateTime) {
        this.upDateTime = upDateTime;
    }

}
