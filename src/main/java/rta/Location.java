package rta;

public class Location implements Cloneable{
    private int id;
    private String name;
    private boolean init;
    private boolean accept;

    public Location(int id, String name, boolean init, boolean accept) {
        this.id = id;
        this.name = name;
        this.init = init;
        this.accept = accept;
    }

    public Location(int id,String name){
        this.id = id;
        this.name = name;
    }

    public Location(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public boolean isAccept() {
        return accept;
    }

    public void setAccept(boolean accept) {
        this.accept = accept;
    }

    @Override
    public Location clone() throws CloneNotSupportedException {
        return (Location) super.clone();
    }

    @Override
    public String toString(){
        return "id:"+id+" ,name:"+name;
    }

}
