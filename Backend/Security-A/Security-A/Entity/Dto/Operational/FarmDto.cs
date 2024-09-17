namespace Entity.Dto.Operational
{
    public class FarmDto
    {
        public int Id { get; set; }
        public string Name { get; set; }
        public int CityId { get; set; }
        public int UserId { get; set; }
        public Boolean State { get; set; }
        public string Addres { get; set; }
        public int Dimension { get; set; }
    }
}
