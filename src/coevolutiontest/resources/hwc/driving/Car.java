
package driving;

class Car extends CarTOP {

  @Override
  public long getMeters(long kmeters) {
    return kmeters * 1000;
  }

  //hwc

  public void setKilometers(long kilometers) {
    this.kilometers = kilometers;
  }

  public long getKilometers() {
    return kilometers;
  }

  public void setProductionYear(int productionYear) {
    this.productionYear = productionYear;
  }

  public int getProductionYear() {
    return productionYear;
  }

}


