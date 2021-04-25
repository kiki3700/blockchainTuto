import java.security.*;
import java.util.ArrayList;

public class Transaction {
	
	public String transactionId; // this is also the hash of the transaction.
	public static PublicKey sender; // senders address/public key.
	public static PublicKey reciepient; // Recipients address/public key.
	public static float value;
	public static byte[] signature; // this is to prevent anybody else from spending funds in our wallet.
	
	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
	
	private static int sequence = 0; // a rough count of how many transactions have been generated. 
	
	// Constructor: 
	public Transaction(PublicKey from, PublicKey to, float value,  ArrayList<TransactionInput> inputs) {
		this.sender = from;
		this.reciepient = to;
		this.value = value;
		this.inputs = inputs;
	}
	
	// This Calculates the transaction hash (which will be used as its Id)
	private String calulateHash() {
		sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
		return StringUtil.applySha256(
				StringUtil.getStringFromKey(sender) +
				StringUtil.getStringFromKey(reciepient) +
				Float.toString(value) + sequence
				);
	}
	

	//Signs all the data we dont wish to be tampered with.
	public static void generateSignature(PrivateKey privateKey) {
		String data = StringUtil.getStringFromKey(sender)
				+ StringUtil.getStringFromKey(reciepient)
				+ Float.toString(value)	;
		
		signature = StringUtil.applyECDSASig(privateKey,data);		
	}
	//Verifies the data we signed hasnt been tampered with
	public boolean verifiySignature() {
		String data = StringUtil.getStringFromKey(sender)
				+ StringUtil.getStringFromKey(reciepient)
				+ Float.toString(value)	;
		return StringUtil.verifyECDSASig(sender, data, signature);
	}
	
	public boolean processTransaction() {
		if(verifiySignature() == false) {
			System.out.println("#Transaction Signature fialed to verify");
			return false;
		}
		
		//gather transaction inputs (Make sure they are unspent
		for(TransactionInput i : inputs) {
			i.UTXO = BlockChain.UTXOs.get(i.transactionOutputId);
		}
		
		//check if transaction is valid:
		if(getInputValue() < BlockChain.minimumTransaction) {
			System.out.println("Transaction Inputs to small" + getInputValue());
			return false;
		}
		
		//generate transaction outputs:
		float leftOver = getInputValue() - value; // get value of inputs then the leftover change;
		transactionId = calulateHash();
		outputs.add(new TransactionOutput(this.reciepient,value,transactionId));// sendvalue to reciepient
		outputs.add(new TransactionOutput( this.sender, leftOver,transactionId));//send the left over 'change ' back to sender
		
		//add outputs to Unspent list
		for(TransactionOutput o : outputs) {
			BlockChain.UTXOs.put(o.id, o);
		}
		
		//remove transction inputs from UTXO lists as spents:
		for(TransactionInput i : inputs) {
				if(i.UTXO==null) continue; // if Transaction can't be found skip it
				BlockChain.UTXOs.remove(i.UTXO.id);
			}
			return true;
		}
		//returns sum of input (UTXOs) values
		public float getInputValue() {
			float total = 0;
			for(TransactionInput i : inputs) {
				if(i.UTXO == null) continue;
				total += i.UTXO.value;
			}
			return total;
		}
		
		//return sum of outputs:
		public float getOutputsValue() {
			float total = 0;
			for(TransactionOutput o : outputs) {
				total += o.value;
			}
			return total;
	}
}