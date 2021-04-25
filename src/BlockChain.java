import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.GsonBuilder;

public class BlockChain {
	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static HashMap<String, TransactionOutputs> UTXOs = new HashMap<String, TransactionOutputs>();
	//list of all unspent transctions.
	public static int difficulty = 3;
	public static Wallet walletA;
	public static Wallet walletB;
	
	public static void main(String[] args) {
//		Block genesisBlock = new Block("Hi im the first block","0");
//		System.out.println("Hash for block 1"+genesisBlock.hash);
//		Block secondBlock = new Block("yo im the second block", genesisBlock.hash);
//		System.out.println("Hash for block 2"+secondBlock.hash);
//		Block thirdBlock = new Block("Hey im the third block",secondBlock.hash);
//		System.out.println("Hash for block 3"+thirdBlock.hash);
		
		//add our blocks to the blockchain Arraylist:
//		blockchain.add(new Block("Hi im the first block", "0"));
//		System.out.println("try to mine block1 ...");
//		blockchain.get(0).mineBlock(difficulty);
//		
//		blockchain.add(new Block("Hi im the second block", blockchain.get(blockchain.size()-1).hash));
//		System.out.println("try to mine block2 ...");
//		blockchain.get(1).mineBlock(difficulty);
//		
//		blockchain.add(new Block("Hi im the third block", blockchain.get(blockchain.size()-1).hash));
//		System.out.println("try to mine block3 ...");
//		blockchain.get(2).mineBlock(difficulty);
//		
//		System.out.println("\nBlockchain is valid: "+isChainValid());
//		
//		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
//		System.out.println("\nThe block chain: ");
//		System.out.println(blockchainJson);
		
		//setup Bouncey castle as a Security Provide
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		//Create the new Wallets
		walletA  = new Wallet();
		walletB = new Wallet();
		
		//Test public and private keys
		System.out.println("private and public key");
		System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
		System.out.println(StringUtil.getStringFromKey(walletB.privateKey));
		//create a test transaction from WalletA to WalletB
		Transaction transcation = new Transaction(walletA.publicKey,walletB.publicKey, 5 , null);
		Transaction.generateSignature(walletA.privateKey);
		//Verify the signature works and verify it from the public key
		System.out.println("Is signature verified");
		System.out.println(transcation.verifiySignature());
	}
		
		
		
		
				
	public static Boolean isChainValid() {
		Block currentBlock; 
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		
		//loop through blockchain to check hashes:
		for(int i=1; i < blockchain.size(); i++) {
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i-1);
			//compare registered hash and calculated hash:
			if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
				System.out.println("Current Hashes not equal");			
				return false;
			}
			//compare previous hash and registered previous hash
			if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
				System.out.println("Previous Hashes not equal");
				return false;
			}
			//check if hash is solved
			if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
				System.out.println("This block hasn't been mined");
				return false;
			}
		}
		return true;
	}
	

}
