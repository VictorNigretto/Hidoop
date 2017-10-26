package map;


// L'objet contenant un mapper et un reducer
// C'est lui qu'on envoit au Job pour qu'il puisse faire son Job.startJob
public interface MapReduce extends Mapper, Reducer {
}
