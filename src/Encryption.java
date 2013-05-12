import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Encryption
{



	public static void main(String[] args)
	{
			Security.addProvider(new BouncyCastleProvider());

		    String plainText = "This is the chat message";
		    System.out.println("plaintext: " + plainText);

		    String key = "000102030405060708090A0B0C0D0E0F";
		    System.out.println("key: " + key);

		    String cipherText = encrypt(plainText, key);
		    System.out.println("ciphertext: " + cipherText);

		    String decrypted = decrypt(cipherText, key);



		    if (decrypted != null && decrypted.equals(plainText)) {
		        System.out.println("Decryption: " + decrypted);
		    } else {
		        System.out.println("Decryption Failed!");
    }

	}


public static String encrypt(final String plainText, final String symKeyHex) {

        final byte[] symKeyData = DatatypeConverter.parseHexBinary(symKeyHex);
        final byte[] encodedMessage = plainText.getBytes(Charset.forName("UTF-8"));

        try {

        	//create new cipher using AES 128 CBC

            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");
            final int blockSize = cipher.getBlockSize();

            // generate the key

            final SecretKeySpec symKey = new SecretKeySpec(symKeyData, "AES");

            // generate the iv

            final byte[] ivData = new byte[blockSize];
            final SecureRandom rnd = SecureRandom.getInstance("SHA1PRNG");
            rnd.nextBytes(ivData);
            final IvParameterSpec iv = new IvParameterSpec(ivData);



            // encrypt the cipher

            cipher.init(Cipher.ENCRYPT_MODE, symKey, iv);
            final byte[] cipherText = cipher.doFinal(encodedMessage);

            // concatenate IV and encrypted message

            final byte[] ivCipherText = new byte[ivData.length + cipherText.length];
            System.arraycopy(ivData, 0, ivCipherText, 0, blockSize);
            System.arraycopy(cipherText, 0, ivCipherText, blockSize, cipherText.length);
            final String ivCipherTextBase64 = DatatypeConverter.printBase64Binary(ivCipherText);

            //return the cipher text in Base 64

            return ivCipherTextBase64;


        }//try

        catch (InvalidKeyException e) {
            throw new IllegalArgumentException(
                    "key argument does not contain a valid AES key");
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(
                    "Unexpected exception during encryption", e);
        }


    }

    public static String decrypt(final String ivCipherTextBase64, final String symKeyHex) {

        final byte[] symKeyData = DatatypeConverter.parseHexBinary(symKeyHex);
        final byte[] ivCipherText = DatatypeConverter.parseBase64Binary(ivCipherTextBase64);


        try {

            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding","BC");
            final int blockSize = cipher.getBlockSize();

            // create the key
            final SecretKeySpec symKey = new SecretKeySpec(symKeyData, "AES");

            // retrieve random IV from start of the received message

            final byte[] ivData = new byte[blockSize];
            System.arraycopy(ivCipherText, 0, ivData, 0, blockSize);
            final IvParameterSpec iv = new IvParameterSpec(ivData);

            // retrieve the cipher text
            final byte[] cipherText = new byte[ivCipherText.length - blockSize];
            System.arraycopy(ivCipherText, blockSize, cipherText, 0, cipherText.length);

            cipher.init(Cipher.DECRYPT_MODE, symKey, iv);

            final byte[] encodedMessage = cipher.doFinal(cipherText);

            // concatenate IV and encrypted message
            final String plainText = new String(encodedMessage, Charset.forName("UTF-8"));

            return plainText;



        }//try
        catch (InvalidKeyException e) {
            throw new IllegalArgumentException(
                    "key argument does not contain a valid AES key");
        } catch (BadPaddingException e) {
            // you'd better know about padding oracle attacks
            return null;
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(
                    "Unexpected exception during decryption", e);
        }
    }


}
