// Copyright (c) 2018 The Midi2Drum developers
// Distributed under the MIT software license, see the accompanying
// file COPYING or http://www.opensource.org/licenses/mit-license.php.

package midi2drum;

import java.io.File;
import javax.swing.filechooser.*;

public class MidiFileFilter extends FileFilter {
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
            if (extension.equals("mid")) {
                    return true;
            } else {
                return false;
            }
        }
        return false;
    }

    //The description of this filter
    public String getDescription() {
        return "*.mid";
    }	

}
