package com.swst.tools;

import lombok.Data;

import java.util.List;

/**
 * 树形类
 * @author yxh
 * @date 2019-12-07 14:20
 */

@Data
public class TreeFile {
    /**
     * parent
     */
    private TreeFile parentIndex;
    /**
     * index
     */
    private String index;
    /**
     * level
     */
    private TreeFile levelIndex;
    /**
     * child
     */
    private List childIndex;

}
