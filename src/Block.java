import java.security.MessageDigest;
import java.util.Date;
class Block {
	public String hash;
	public String previousHash;
	private String data; // data will be simple message
	private long timeStamp; //
	private int nonce;
	

		//Block Constructor.  
		public Block(String data,String previousHash ) {
			this.data = data;
			this.previousHash = previousHash;
			this.timeStamp = new Date().getTime();
			
			this.hash = calculateHash(); //Making sure we do this after we set the other values.
		}
		
		//Calculate new hash based on blocks contents
		public String calculateHash() {
			String calculatedhash = StringUtil.applySha256( 
					previousHash +
					Long.toString(timeStamp) +
					Integer.toString(nonce) + 
					data 
					);
			return calculatedhash;
		}
		
		public void mineBlock(int difficulty) {
			String target = new String(new char[difficulty]).replace('\0', '0'); //Create a string with difficulty * "0" 
			while(!hash.substring( 0, difficulty).equals(target)) {
				nonce ++;
				hash = calculateHash();
			}
			System.out.println("Block Mined!!! : " + hash);
		}
}