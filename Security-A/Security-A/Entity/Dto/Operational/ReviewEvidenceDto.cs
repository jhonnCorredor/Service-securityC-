namespace Entity.Dto.Operational
{
    public class ReviewEvidenceDto
    {
        public int Id { get; set; }
        public int EvidenceId { get; set; }
        public int ReviewId { get; set; }
        public Boolean State { get; set; }
    }
}
