import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {
	public PrivateKey privateKey;
	public PublicKey publicKey;
	
	public HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();
	//only UTXOs onwed by this wallet
	
	public Wallet() {
		generateKeyPair();
	}
	public void generateKeyPair() {
		try {
			KeyPairGenerator KeyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			//initialize the key generator and generate a keypair
			KeyGen.initialize(ecSpec, random);//256 bytes provides an acceptable
			KeyPair keyPair = KeyGen.generateKeyPair();
			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	//generates balance and stroes the UTXO;s owned by this wallet in this.UTXOs
	public float getBalance() {
		float total = 0;
	for(Map.Entry<String, TransactionOutput> item : BlockChain.UTXOs.entrySet()) {
		TransactionOutput UTXO = item.getValue();
		if(UTXO.isMine(publicKey)) {
			//if output belongs to me(if coin beleng to me)
			UTXOs.put(UTXO.parentTransactionId, UTXO);
			//add it to our list of unspent transactions
			total += UTXO.value;
		}
	}
	return total;
	}
	//Generates and returns a mew tranaction from this wallet.
	public Transaction sendFunds(PublicKey _recipient, float value) {
		if(getBalance()< value) {
			// gether balance and check funds.
			System.out.println("#Not enough funds to send transaction and funds. Transaction Discarded");
			return null;
	}
	
	//create arrat list of inputs
	ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	
	float total = 0;
	
	for(Map.Entry<String, TransactionOutput> item : UTXOs.entrySet()) {
		TransactionOutput UTXO = item.getValue();
		total += UTXO.value;
		inputs.add(new TransactionInput(UTXO.id));
		if(total> value) break;
		}
	Transaction newTransaction = new Transaction(publicKey, _recipient, value, inputs);
	
	for(TransactionInput input: inputs) {
		UTXOs.remove(input.transactionOutputId);
	}
	return newTransaction;
	}
	
}
