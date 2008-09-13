package edu.internet2.middleware.morphString;


/**
 * expose methods of rijndael
 * @version $Id: TwoWayEncrypt.java,v 1.1 2008-09-13 18:51:48 mchyzer Exp $
 * @author mchyzer
 */
public class TwoWayEncrypt {

  /**
   * decrypt a string based on key
   * @param key
   * @param in
   * @return the original string
   */
  public static String decrypt(String key, String in) {
    RijnMaker r1 = new RijnMaker(16, 16);

    return r1.decrypt(in, key).trim();
  }

  /**
   * encrypt a string based on key
   * @param key
   * @param in
   * 
   * @return encrypted String
   */
  public static String encrypt(String key, String in) {
    RijnMaker r1 = new RijnMaker(16, 16);

    return r1.encrypt(in, key).trim();
  }


}
