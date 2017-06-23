package core;



public class BFSingleton {
    private static final BFSingleton INSTANCE = new BFSingleton();

    private String ButtonListenerStatus;
    
    
    private BFSingleton() {
    	
    }

    public static BFSingleton getInstance() {
        return INSTANCE;
    }

	public String getButtonListenerStatus() {
		return ButtonListenerStatus;
	}

	public void setButtonListenerStatus(String buttonListenerStatus) {
		ButtonListenerStatus = buttonListenerStatus;
	}
}