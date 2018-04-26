// Copyright (c) 2018 The Midi2Drum developers
// Distributed under the MIT software license, see the accompanying
// file COPYING or http://www.opensource.org/licenses/mit-license.php.

package midi2drum;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class PictureFileFilter extends FileFilter{
	  //Accept all directories and all gif, jpg, tiff, or png files.
  public boolean accept(File f) {
      if (f.isDirectory()) {
          return true;
      }
      String s = f.getName();
      int i = s.lastIndexOf('.');

      String extension = null;
      if (i > 0 &&  i < s.length() - 1) {
      	extension = s.substring(i+1).toLowerCase();
      }
      if (extension != null) {
          if (extension.equals("tiff") ||
                  extension.equals("tif") ||
                  extension.equals("gif") ||
                  extension.equals("jpeg") ||
                  extension.equals("jpg") ||
                  extension.equals("png")) {
                  return true;
          } else {
              return false;
          }
      }
      return false;
  }

  //The description of this filter
  public String getDescription() {
      return "*.tiff,*.tif,*.gif,*.jpeg,*.jpg,*.png";
  }	
}
