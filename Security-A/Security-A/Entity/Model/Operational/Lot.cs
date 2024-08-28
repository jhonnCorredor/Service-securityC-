using Entity.Model.Parameter;

namespace Entity.Model.Operational
{
    public class Lot
    {
        public int Id { get; set; }
        public int CropId { get; set; }
        public Crop Crop { get; set; }
        public int FarmId { get; set; }
        public Farm Farm { get; set; }
        public int Num_hectareas { get; set; }
        public bool State { get; set; }
        public DateTime Created_at { get; set; }
        public DateTime? Updated_at { get; set; }
        public DateTime? Deleted_at { get; set; }
    }
}
