/**
 *
 */
package com.ns.mutiphotochoser.utils;


import com.ns.mutiphotochoser.model.ImageBean;

/**
 * @author xiaolf1
 */
public interface ChoseImageListener {

    public boolean onSelected(ImageBean image);

    public boolean onCancelSelect(ImageBean image);
}
