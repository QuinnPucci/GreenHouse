package tme3;
// easiest just to include this in tme3 since it's already imported to greenhouse controls

public interface Fixable {

    // the two abstract methods that will have to be defined in PowerOn and FixWindow
    void fix();
    void log();
    // PowerOn and FixWindow located in GreenHouseControls to access variables and other methods.
}
