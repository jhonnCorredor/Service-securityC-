namespace Entity.Dto.Operational
{
    public class LotFertilizationDto
    {
        public int Id { get; set; }
        public int LotId { get; set; }
        public int FertilizationId { get; set; }
        public Boolean State { get; set; }
    }
}
