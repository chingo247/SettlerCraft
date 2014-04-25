/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.core.util;

import com.google.common.base.Preconditions;

/**
 *
 * @author Christian
 */
public class Maths {
  
  public static int lowest(int... numbers) {
    Preconditions.checkArgument(numbers.length > 0);
    if(numbers.length == 1) return numbers[0];
    
    int target = numbers[0];
    for (int i = 1; i < numbers.length; i++) {
      target = Math.min(target, numbers[i]);
    }
    return target;
  }
  
  public static int highest(int... numbers) {
    Preconditions.checkArgument(numbers.length > 0);
    if(numbers.length == 1) return numbers[0];
    
    int target = numbers[0];
    for (int i = 1; i < numbers.length; i++) {
      target = Math.max(target, numbers[i]);
    }
    return target;
  }
  
  
}
