namespace Entity.Dto.Operational
{
    public class LotFumigationDto
    {
        public int Id { get; set; }
        public int LotId { get; set; }
        public int FumigationId { get; set; }
        public Boolean State { get; set; }
    }
}
